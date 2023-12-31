from fastapi import FastAPI, Request
from transformers import AutoTokenizer, AutoModel
import uvicorn, json, datetime
import torch
import argparse

try:
    import chatglm_cpp
    enable_chatglm_cpp = True
except:
    print("[WARN] chatglm-cpp not found. Install it by `pip install chatglm-cpp` for better performance. "
          "Check out https://github.com/li-plus/chatglm.cpp for more details.")
    enable_chatglm_cpp = False

LANGUAGE_TAG = {
    "Abap"         : "* language: Abap",
    "ActionScript" : "// language: ActionScript",
    "Ada"          : "-- language: Ada",
    "Agda"         : "-- language: Agda",
    "ANTLR"        : "// language: ANTLR",
    "AppleScript"  : "-- language: AppleScript",
    "Assembly"     : "; language: Assembly",
    "Augeas"       : "// language: Augeas",
    "AWK"          : "// language: AWK",
    "Basic"        : "' language: Basic",
    "C"            : "// language: C",
    "C#"           : "// language: C#",
    "C++"          : "// language: C++",
    "CMake"        : "# language: CMake",
    "Cobol"        : "// language: Cobol",
    "CSS"          : "/* language: CSS */",
    "CUDA"         : "// language: Cuda",
    "Dart"         : "// language: Dart",
    "Delphi"       : "{language: Delphi}",
    "Dockerfile"   : "# language: Dockerfile",
    "Elixir"       : "# language: Elixir",
    "Erlang"       : f"% language: Erlang",
    "Excel"        : "' language: Excel",
    "F#"           : "// language: F#",
    "Fortran"      : "!language: Fortran",
    "GDScript"     : "# language: GDScript",
    "GLSL"         : "// language: GLSL",
    "Go"           : "// language: Go",
    "Groovy"       : "// language: Groovy",
    "Haskell"      : "-- language: Haskell",
    "HTML"         : "<!--language: HTML-->",
    "Isabelle"     : "(*language: Isabelle*)",
    "Java"         : "// language: Java",
    "JavaScript"   : "// language: JavaScript",
    "Julia"        : "# language: Julia",
    "Kotlin"       : "// language: Kotlin",
    "Lean"         : "-- language: Lean",
    "Lisp"         : "; language: Lisp",
    "Lua"          : "// language: Lua",
    "Markdown"     : "<!--language: Markdown-->",
    "Matlab"       : f"% language: Matlab",
    "Objective-C"  : "// language: Objective-C",
    "Objective-C++": "// language: Objective-C++",
    "Pascal"       : "// language: Pascal",
    "Perl"         : "# language: Perl",
    "PHP"          : "// language: PHP",
    "PowerShell"   : "# language: PowerShell",
    "Prolog"       : f"% language: Prolog",
    "Python"       : "# language: Python",
    "R"            : "# language: R",
    "Racket"       : "; language: Racket",
    "RMarkdown"    : "# language: RMarkdown",
    "Ruby"         : "# language: Ruby",
    "Rust"         : "// language: Rust",
    "Scala"        : "// language: Scala",
    "Scheme"       : "; language: Scheme",
    "Shell"        : "# language: Shell",
    "Solidity"     : "// language: Solidity",
    "SPARQL"       : "# language: SPARQL",
    "SQL"          : "-- language: SQL",
    "Swift"        : "// language: swift",
    "TeX"          : f"% language: TeX",
    "Thrift"       : "/* language: Thrift */",
    "TypeScript"   : "// language: TypeScript",
    "Vue"          : "<!--language: Vue-->",
    "Verilog"      : "// language: Verilog",
    "Visual Basic" : "' language: Visual Basic",
}

app = FastAPI()
def device(config, model_path):
    if enable_chatglm_cpp and config.use_chatglm_cpp:
        print("Using chatglm-cpp to improve performance")
        dtype = "f16" if config.half else "f32"
        if config.quantize in [4, 5, 8]:
            dtype = f"q{config.quantize}_0"
        model = chatglm_cpp.Pipeline(model_path, dtype=dtype)
        return model

    print("chatglm-cpp not enabled, falling back to transformers")
    if config.device != "cpu":
        if not config.half:
            model = AutoModel.from_pretrained(model_path, trust_remote_code=True).cuda(int(config.device))
        else:
            model = AutoModel.from_pretrained(model_path, trust_remote_code=True).cuda(int(config.device)).half()
        if config.quantize in [4, 8]:
            print(f"Model is quantized to INT{config.quantize} format.")
            model = model.half().quantize(config.quantize)
    else:
        model = AutoModel.from_pretrained(model_path, trust_remote_code=True)
    return model.eval()

@app.post("/")
async def create_item(request: Request):
    global model, tokenizer
    json_post_raw = await request.json()
    json_post = json.dumps(json_post_raw)
    json_post_list = json.loads(json_post)
    lang = json_post_list.get('lang')
    prompt = json_post_list.get('prompt')
    max_length = json_post_list.get('max_length', 128)
    top_p = json_post_list.get('top_p', 0.95)
    temperature = json_post_list.get('temperature', 0.2)
    top_k = json_post_list.get('top_k', 0)
    if lang != "None":
        prompt = LANGUAGE_TAG[lang] + "\n" + prompt
    if enable_chatglm_cpp and use_chatglm_cpp:
        response = model.generate(prompt,
                                  max_length=max_length,
                                  do_sample=temperature > 0,
                                  top_p=top_p,
                                  top_k=top_k,
                                  temperature=temperature)
    else:
        response = model.chat(tokenizer,
                              prompt,
                              max_length=max_length,
                              top_p=top_p,
                              top_k=top_k,
                              temperature=temperature)
    now = datetime.datetime.now()
    time = now.strftime("%Y-%m-%d %H:%M:%S")
    answer = {
        "response": response,
        "lang": lang,
        "status": 200,
        "time": time
    }

    return answer


def api_start(config):
    global use_chatglm_cpp
    use_chatglm_cpp = config.use_chatglm_cpp
    model_path = "CodeModels/CodeGeex2"
    global tokenizer
    global model
    tokenizer = AutoTokenizer.from_pretrained(model_path, trust_remote_code=True)
    model = device(config, model_path)
    uvicorn.run(app, host="0.0.0.0", port=7861, workers=1)

