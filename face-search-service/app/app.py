# coding: utf-8
import base64
import json
import re
from io import BytesIO

import face_recognition
import numpy as np
import tensorflow as tf
from PIL import Image
from flask import Flask, jsonify, request, redirect, abort, Response
from flask_cors import CORS

import face_encoding_data_service
import train_model_service


app = Flask(__name__, static_url_path='', static_folder='dist')
CORS(app, supports_credentials=True)


@app.route('/person/register', methods=['GET', 'POST'])
def person_register():
    if request.method == 'POST':
        base64_img = json.loads(request.get_data())['imgData']
        person_id = json.loads(request.get_data())['personId']
        open_key = json.loads(request.get_data())['openKey']
        for i in range(len(base64_img)):
            base64_data = re.sub('^data:image/.+;base64,', '', base64_img[i])
            img = base64.b64decode(base64_data)
            img_data = BytesIO(img)
            im = Image.open(img_data)
            im = im.convert('RGB')
            img_array = np.array(im)
            faces = face_recognition.face_locations(img_array)
            if len(faces) > 0:
                face_encoding_data_service.save_person_encoding(person_id, json.dumps(faces[0].tolist()))
        train_labels, train_datas = face_encoding_data_service.list_face_encoding(open_key)
        if len(train_labels) > 0 and len(train_datas) > 0:
            train_model_service.train_model(train_labels, train_datas, get_open_key_model_path(open_key))
    data = {
        'result': True,
        'msg': 'SUCCESS',
        'code': 200
    }
    return json.dumps(data)


@app.route('/person/search', methods=['GET', 'POST'])
def person_search():
    if request.method == 'POST':
        base64_img = json.loads(request.get_data())['img']
        open_key = json.loads(request.get_data())['openKey']
        img = base64.b64decode(base64_img)
        img_data = BytesIO(img)
        im = Image.open(img_data)
        im = im.convert('RGB')
        image = np.array(im)
        result = predict_image(image, open_key)
        data = {
            'result': result,
            'msg': 'SUCCESS',
            'code': 200
        }
        return json.dumps(data)


def predict_image(image, open_key):
    model = tf.keras.models.load_model(get_open_key_model_path(open_key))
    face_encode = face_recognition.face_encodings(image)
    result = []
    for j in range(len(face_encode)):
        predictions1 = model.predict(np.array(face_encode[j]).reshape(1, 128))
        print(predictions1)
        if np.max(predictions1[0]) > 0.90:
            print(np.argmax(predictions1[0]).dtype)
            person_id = int(np.argmax(predictions1[0]))
            print('第%d张脸是%s' % (j+1,person_id))
            result.append(person_id)
    return result


def get_open_key_model_path(open_key):
    return 'model/' + open_key + '_model.h5'


@app.route('/api/test', methods=['GET', 'POST'])
def api_test():
    data = {
        'result': True,
        'msg': 'SUCCESS',
        'code': 200
    }
    return json.dumps(data)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5002, debug=True)
