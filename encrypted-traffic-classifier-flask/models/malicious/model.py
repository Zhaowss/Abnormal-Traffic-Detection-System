import torch
import torch.nn as nn
import torch.nn.functional as F


class MyModel(nn.Module):
    def __init__(self):
        super(MyModel, self).__init__()

        # # 空间特征提取
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=32, kernel_size=3, stride=1, padding=1)
        self.pool1 = nn.MaxPool2d(kernel_size=2, stride=2)
        self.conv2 = nn.Conv2d(in_channels=32, out_channels=64, kernel_size=3, stride=1, padding=1)
        self.pool2 = nn.MaxPool2d(kernel_size=2, stride=2)
        self.dropout = nn.Dropout(0.5)
        self.relu = nn.ReLU()
        self.classifer = nn.Linear(64 * 7 * 7, 12)


        # # 时序特征提取
        self.conv_t = nn.Conv1d(1, 32, kernel_size=5, stride=1, padding=2)
        self.pool_t = nn.MaxPool1d(2)
        self.conv_t2 = nn.Conv1d(32, 64, kernel_size=5, stride=1, padding=2)
        self.pool_t2 = nn.MaxPool1d(2)
        self.dropout = nn.Dropout(0.3)
        self.gru = nn.GRU(input_size=196, hidden_size=64, num_layers=1, bidirectional=True, dropout=0.3)
        self.fc = nn.Linear(64 * 2, 12)


    def forward(self, x):

        # # 空间特征提取
        out_s = self.conv1(x)
        out_s = self.pool1(out_s)
        out_s = self.dropout(out_s)
        out_s = self.conv2(out_s)
        out_s = self.pool2(out_s)
        out_s = self.dropout(out_s)
        out_s = out_s.view(x.shape[0], -1)
        out_s = self.classifer(out_s)

        # 时序特征提取
        out_t = x.view(-1, 1, 784)
        out_t = self.conv_t(out_t)
        out_t = F.relu(out_t)
        out_t = self.pool_t(out_t)
        out_t = self.dropout(out_t)
        out_t = self.conv_t2(out_t)
        out_t = F.relu(out_t)
        out_t = self.pool_t2(out_t)
        out_t = self.dropout(out_t)
        # # slstm层的输出
        out_t, _ = self.gru(out_t)

        # 通过全连接层得到最终输出
        out_t = self.fc(out_t[:, -1, :])

        out = 1.2 * out_s + 0.8 * out_t

        return out