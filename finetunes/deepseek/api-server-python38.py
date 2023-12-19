import os
from threading import Thread
from typing import Iterator, List, Tuple

import uvicorn
from fastapi import FastAPI, Response
from fastapi.responses import StreamingResponse

import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, TextIteratorStreamer
import requests
from pydantic import BaseModel

MAX_MAX_NEW_TOKENS = 2048
DEFAULT_MAX_NEW_TOKENS = 1024
total_count = 0
MAX_INPUT_TOKEN_LENGTH = int(os.getenv("MAX_INPUT_TOKEN_LENGTH", "4096"))

if torch.cuda.is_available():
    model_id = "/openbayes/input/input0/"
    model = AutoModelForCausalLM.from_pretrained(model_id, torch_dtype=torch.bfloat16, device_map="auto")
    tokenizer = AutoTokenizer.from_pretrained(model_id)
    tokenizer.use_default_system_prompt = False


def generate(
        chat_history: List[Tuple[str, str]],
        max_new_tokens: int = 512,
        temperature: float = 0.1,
        top_p: float = 0.9,
        top_k: int = 50,
        repetition_penalty: float = 1,
) -> Iterator[str]:
    global total_count
    total_count += 1
    if total_count % 50 == 0:
        os.system("nvidia-smi")

    conversation = chat_history

    input_ids = tokenizer.apply_chat_template(conversation, return_tensors="pt")
    if input_ids.shape[1] > MAX_INPUT_TOKEN_LENGTH:
        input_ids = input_ids[:, -MAX_INPUT_TOKEN_LENGTH:]
    input_ids = input_ids.to(model.device)

    streamer = TextIteratorStreamer(tokenizer, timeout=10.0, skip_prompt=True, skip_special_tokens=True)
    generate_kwargs = dict(
        {"input_ids": input_ids},
        streamer=streamer,
        max_new_tokens=max_new_tokens,
        do_sample=False,
        top_p=top_p,
        top_k=top_k,
        num_beams=1,
        temperature=temperature,
        repetition_penalty=repetition_penalty,
        eos_token_id=32021
    )
    t = Thread(target=model.generate, kwargs=generate_kwargs)
    t.start()

    outputs = []
    for text in streamer:
        outputs.append(text)
        yield "".join(outputs).replace("<|EOT|>", "")


app = FastAPI()


class Message(BaseModel):
    role: str
    content: str


@app.post("/api/chat", response_class=Response)
async def root(msgs: List[Message]) -> StreamingResponse:
    return StreamingResponse(generate(msgs), media_type="text/event-stream")


if __name__ == "__main__":
    try:
        meta = requests.get('http://localhost:21999/gear-status', timeout=5).json()
        url = meta['links'].get('auxiliary')
        if url:
            print("打开该链接访问:", url)
    except Exception:
        pass

    uvicorn.run(app, host="0.0.0.0", port=8080)
