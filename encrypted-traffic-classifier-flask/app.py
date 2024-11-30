import uuid
import torch
import torch.nn.functional as F
import torchvision.transforms as transforms

from PIL import Image
from pathlib import Path
from loguru import logger
from flask_cors import CORS
from werkzeug.datastructures import FileStorage
from flask import Flask, jsonify, request, Response

from models.benign.model import MyModel as benignModel
from models.malicious.model import MyModel as maliciousModel

app = Flask(__name__)
cors = CORS(app)
FILE_DIR = Path(__file__).parent / Path('files')
if not FILE_DIR.is_dir():
    FILE_DIR.mkdir(parents=True, exist_ok=True)

LOG_DIR = Path(__file__).parent / Path('logs')
if not LOG_DIR.is_dir():
    LOG_DIR.mkdir(parents=True, exist_ok=True)
LOG_PATH = LOG_DIR / Path('app.log')

@app.route('/classify', methods=['POST'])
def classify() -> tuple[Response, int]:
    task_type: str = request.form.get('taskType')
    image_file: FileStorage = request.files.get('imageFile')
    if task_type is None or image_file is None:
        return jsonify({'code': 400, 'msg': '请求参数缺失', 'data': None}), 400

    logger.info('任务类型：' + task_type)
    logger.info('图片文件：' + image_file.filename)

    temp = uuid.uuid4().hex
    temp_dir = FILE_DIR / Path(temp)
    if not temp_dir.is_dir():
        temp_dir.mkdir(parents=True, exist_ok=True)
    image_file_path = temp_dir / Path(image_file.filename)

    try:
        image_file.save(image_file_path)
        while (True):
            if image_file_path.exists() is True:
                break
        category, confidence = inference(task_type, image_file_path)

        logger.info('类别：' + category)
        logger.info('置信度：' + confidence)

    except Exception as e:
        logger.error(str(repr(e)))
        return jsonify({'code': 500, 'msg': '分类失败', 'data': None}), 500

    return jsonify(
        {'code': 200, 'msg': '分类成功', 'data': {'category': category, 'confidence': confidence}}), 200


def inference(task_type: str, image_file_path: Path) -> tuple[str, str]:
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    transform = transforms.Compose([
        transforms.Resize((28, 28)),
        transforms.ToTensor()
    ])
    image_file = Image.open(image_file_path)
    image_tensor = transform(image_file).unsqueeze(0).to(device)

    if task_type == "0":
        model = benignModel().to(device)
        best_model_path = Path(__file__).parent / Path('weights') / Path('benign') / Path(
            'best_valid_model_weights_94.7588.pth')
        dict_2class = ['Chat', 'Email', 'File', 'Streaming', 'Voip', 'Vpn_Chat', 'Vpn_Email', 'Vpn_File', 'Vpn_P2P',
                       'Vpn_Streaming', 'Vpn_Voip']
    elif task_type == "1":
        model = maliciousModel().to(device)
        best_model_path = Path(__file__).parent / Path('weights') / Path('malicious') / Path(
            'best_valid_model_weights_97.4468.pth')
        dict_2class = ['ADload', 'Adware', 'Artemis', 'Bunitu', 'Dridex', 'OpenCandy', 'Razy', 'Tinba', 'TrickBot',
                       'Virtue', 'Wannacry', 'Zbot']
    else:
        return 'Error', '0'
    model.load_state_dict(torch.load(best_model_path, map_location=device))
    model.eval()

    with torch.no_grad():
        output = model(image_tensor)
        probabilities = F.softmax(output, dim=1)
        _, predicted = torch.max(probabilities, 1)

        confidence = {}
        for i, prob in enumerate(probabilities[0]):
            confidence[dict_2class[i]] = prob.item()
        logger.info(confidence)

    return dict_2class[predicted.item()], str(confidence)


if __name__ == '__main__':


    logger.add(
        sink=LOG_PATH,
        level='INFO',
        rotation="100 MB",
        retention='10 days'
    )

    app.config['JSON_AS_ASCII'] = False
    app.run(
        host='0.0.0.0',
        port=5000
    )
