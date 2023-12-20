# DeepSeek

## Fine Tune

步骤参考：[FineTune.ipynb](./finetune.ipynb)

3090 或者 4000 系列的显卡需要设置环境变量，否则会报错：

```bash
export NCCL_P2P_DISABLE=1
export NCCL_IB_DISABLE=1
```

也可以直接在 `finetune_deepseekcoder.py` 中设置：

```python
import os
os.environ["NCCL_P2P_DISABLE"] = "1"
os.environ["NCCL_IB_DISABLE"] = "1"
```

## Server

1.install requirements

```bash
pip install -r requirements.txt
```

2.run server

```bash
python api-server-python38.py
```

3.config AutoDev using Azure config

Custom OpenAI Host: `https://phodal-c0s1mum5qnd5.gear-c1.openbayes.net/api/chat`


## Issue

```bash
drwxr-xr-x 3 root root 4.0K Dec 19 10:45 .
drwxr-xr-x 4 root root 4.0K Dec 19 10:45 ..
-rw-r--r-- 1 root root  733 Dec 19 10:43 config.json
-rw-r--r-- 1 root root  119 Dec 19 10:43 generation_config.json
drwxr-xr-x 2 root root 4.0K Dec 19 10:43 global_step100
-rw-r--r-- 1 root root   14 Dec 19 10:45 latest
-rw-r--r-- 1 root root 4.7G Dec 19 10:43 model-00001-of-00003.safetensors
-rw-r--r-- 1 root root 4.7G Dec 19 10:43 model-00002-of-00003.safetensors
-rw-r--r-- 1 root root 3.4G Dec 19 10:43 model-00003-of-00003.safetensors
-rw-r--r-- 1 root root  24K Dec 19 10:43 model.safetensors.index.json
-rw-r--r-- 1 root root  16K Dec 19 10:45 rng_state_0.pth
-rw-r--r-- 1 root root  16K Dec 19 10:45 rng_state_1.pth
-rw-r--r-- 1 root root  462 Dec 19 10:43 special_tokens_map.json
-rw-r--r-- 1 root root 1.4M Dec 19 10:43 tokenizer.json
-rw-r--r-- 1 root root 5.3K Dec 19 10:43 tokenizer_config.json
-rw-r--r-- 1 root root  11K Dec 19 10:45 trainer_state.json
-rw-r--r-- 1 root root 6.3K Dec 19 10:43 training_args.bin
-rwxr--r-- 1 root root  24K Dec 19 10:44 zero_to_fp32.py
(base) root@phodal-f19njmnkh4s4-main:/openbayes/home/output/checkpoint-100# cd global_step100/
(base) root@phodal-f19njmnkh4s4-main:/openbayes/home/output/checkpoint-100/global_step100# ls -alh
total 76G
drwxr-xr-x 2 root root 4.0K Dec 19 10:43 .
drwxr-xr-x 3 root root 4.0K Dec 19 10:45 ..
-rw-r--r-- 1 root root  38G Dec 19 10:44 bf16_zero_pp_rank_0_mp_rank_00_optim_states.pt
-rw-r--r-- 1 root root  38G Dec 19 10:45 bf16_zero_pp_rank_1_mp_rank_00_optim_states.pt
-rw-r--r-- 1 root root 148K Dec 19 10:43 zero_pp_rank_0_mp_rank_00_model_states.pt
-rw-r--r-- 1 root root 148K Dec 19 10:43 zero_pp_rank_1_mp_rank_00_model_states.pt
```