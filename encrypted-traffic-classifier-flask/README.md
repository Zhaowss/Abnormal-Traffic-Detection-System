# 加密流量分类系统说明文档

## 技术选型

1. 后端：

- `Python 3.9`
- `Flask`
- `Loguru`
- `torch`
- `Pillow`
- `timm`
- `Werkzeug`
- `torchvision`

2. 开发工具：`IntelliJ PyCharm`、`Anaconda`

## 代码结构

```
├─models // 模型代码
│  ├─benign // 加密良性流量
│  └─malicious // 加密恶意流量
├─weights // 模型权重
│  ├─benign // 加密良性流量
│  └─malicious // 加密恶意流量
├─app.py // 系统启动脚本
└─requirements.txt // 系统依赖库
```

## 使用说明

1. 创建虚拟环境`encrypted_traffic_classifier`并输入命令`pip install -r requirements.txt`。
2. 导入项目至`IntelliJ PyCharm`。
3. 运行`app.py`。
4. 或者终端输入命令`python.exe -m flask run`。
5. 或者终端输入命令`python app.py`。