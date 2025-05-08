import sys
import cv2
import numpy as np

# Load YOLO
net = cv2.dnn.readNet("yolov3.weights", "yolov3.cfg")
with open("coco.names", "r") as f:
    classes = [line.strip().upper() for line in f.readlines()]

layer_names = net.getLayerNames()
output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers().flatten()]

# Read image path from argument
img_path = sys.argv[1]
img = cv2.imread(img_path)
height, width, _ = img.shape

blob = cv2.dnn.blobFromImage(img, 1/255.0, (416, 416), swapRB=True, crop=False)
net.setInput(blob)
outs = net.forward(output_layers)

class_ids = []
confidences = []

for out in outs:
    for detection in out:
        scores = detection[5:]
        class_id = np.argmax(scores)
        confidence = scores[class_id]
        if confidence > 0.5:
            class_ids.append(class_id)
            confidences.append(float(confidence))

if class_ids:
    best_class = class_ids[np.argmax(confidences)]
    label = classes[best_class]
    print(label)
else:
    print("UNKNOWN")
