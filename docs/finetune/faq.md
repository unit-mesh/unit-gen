---
layout: default
title: FAQ
parent: FineTune
nav_order: 99
---

# FAQ


## NotImplementedError

NotImplementedError: Using RTX 3090 or 4000 series doesn't support faster communication broadband via P2P or IB. Please
set `NCCL_P2P_DISABLE="1"` and `NCCL_IB_DISABLE="1" or use `accelerate launch` which will do this automatically.

```
export NCCL_P2P_DISABLE=1
export NCCL_IB_DISABLE=1
```

## exits with return code = -9

https://github.com/deepseek-ai/DeepSeek-Coder/issues/55

https://github.com/deepseek-ai/DeepSeek-Coder/issues/54

```bash
 [2023-11-28 10:00:34,590] [ERROR] [launch.py:321:sigkill_handler] ['/home/.python_libs/conda_env/deepseek/bin/python', '-u', 'finetune_deepseekcoder.py', '--local_rank=0', '--model_name_or_path', '/home/project/deepseek/DeepSeek-Coder-main/models/deepseek-coder-6.7b-instruct', '--data_path', '/home/project/deepseek/DeepSeek-Coder-main/data/test.json', '--output_dir', '/home/project/deepseek/DeepSeek-Coder-main/deepseek_finetune', '--num_train_epochs', '1', '--model_max_length', '1024', '--per_device_train_batch_size', '1', '--per_device_eval_batch_size', '1', '--gradient_accumulation_steps', '4', '--evaluation_strategy', 'no', '--save_strategy', 'steps', '--save_steps', '100', '--save_total_limit', '100', '--learning_rate', '2e-5', '--warmup_steps', '10', '--logging_steps', '1', '--lr_scheduler_type', 'cosine', '--gradient_checkpointing', 'True', '--report_to', 'tensorboard', '--deepspeed', 'configs/ds_config_zero3.json', '--bf16', 'True'] exits with return code = -9
```

modify deepspeed config `configs/ds_config_zero3.json`, set `pin_memory` to `false`:

```
"zero_optimization": {
    "stage": 3,
    "offload_optimizer": {
        "device": "cpu",
        "pin_memory": false
    },
    "offload_param": {
        "device": "cpu",
        "pin_memory": false
    },
    ...
}
```