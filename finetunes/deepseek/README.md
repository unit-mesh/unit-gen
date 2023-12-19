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
python server.py
```

