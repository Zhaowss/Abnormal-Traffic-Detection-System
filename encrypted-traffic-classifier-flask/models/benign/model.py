import torch.nn as nn
# from swin_transformer import SwinTransformer
from models.benign.swin_transformer import SwinTransformer


class MyModel(nn.Module):
    def __init__(self):
        super(MyModel, self).__init__()

        self.swint = SwinTransformer(in_chans=1,
                            patch_size=2,
                            window_size=(7, 7),
                            embed_dim=96,
                            depths=(2, 2),
                            num_heads=(2, 4),
                            num_classes=11)


    def forward(self, x):
        out = self.swint(x)
        return out