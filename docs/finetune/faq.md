# NCCL_P2P_DISABLE

NotImplementedError: Using RTX 3090 or 4000 series doesn't support faster communication broadband via P2P or IB. Please
set `NCCL_P2P_DISABLE="1"` and `NCCL_IB_DISABLE="1" or use `accelerate launch` which will do this automatically.


```
export NCCL_P2P_DISABLE=1
export NCCL_IB_DISABLE=1
```