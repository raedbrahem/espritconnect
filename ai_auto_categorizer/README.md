# Enhanced AI Matching System for Lost and Found

This system uses advanced image matching techniques to help users find their lost items by matching them with found items that have been uploaded to the platform.

## Features

- **Image Category Classification**: Automatically categorizes lost items using YOLO object detection
- **Advanced Image Matching**: Uses a hybrid approach combining:
  - Perceptual Hashing (pHash) for overall image similarity
  - SIFT (Scale-Invariant Feature Transform) for feature-based matching
- **Confidence Scoring**: Provides a confidence score for each match
- **Notification System**: Notifies users when a potential match is found

## Setup Instructions

### Prerequisites

- Python 3.8 or higher
- Java 17 or higher
- Spring Boot 3.3.4
- MySQL Database

### Python Dependencies

Install the required Python packages:

```bash
cd ai_auto_categorizer
pip install -r requirements.txt
```

### YOLO Model Files

Download the required YOLO model files:

1. Download `yolov3.weights` from [YOLO website](https://pjreddie.com/darknet/yolo/) and place it in the `ai_auto_categorizer` directory
2. The `yolov3.cfg` and `coco.names` files are already included in the repository

## API Endpoints

### Upload a Lost or Found Item

```
POST /item/upload
```

Parameters:
- `name`: Name of the item
- `description`: Description of the item
- `datePerdu`: Date when the item was lost (ISO format)
- `lieuPerdu`: Location where the item was lost
- `retrouve`: Boolean indicating if this is a found item (true) or a lost item (false)
- `image`: Image file of the item

### Get User's Match Notifications

```
GET /item/matches
```

Returns all match notifications for the authenticated user.

### Get Match Details

```
GET /item/match-details/{matchId}
```

Returns detailed information about a specific match.

### Run Matching on a Specific Proof

```
POST /item/match/{proofId}
```

Manually triggers the matching process for a specific proof.

### Test Matching with URLs

```
GET /item/test-match?proofImageUrl={url}
```

Tests the matching system with a specific image URL.

## How the Matching Works

1. When a user uploads a "found" item, the system:
   - Uploads the image to Cloudinary
   - Creates a Proof object with the image URL
   - Runs the enhanced AI matcher on the proof

2. The enhanced matcher:
   - Downloads the image from Cloudinary
   - Computes perceptual hash (pHash) of the image
   - Extracts SIFT features from the image
   - Compares with all lost items in the database
   - Calculates a combined similarity score
   - Returns the best matches

3. If a good match is found:
   - Creates a notification for the owner of the lost item
   - Sends a push notification if FCM token is available
   - Updates the proof with the similarity score

## Troubleshooting

### Common Issues

1. **Python Script Not Found**: Make sure the path to the Python scripts is correct in the Java services.

2. **YOLO Model Files Missing**: Ensure that `yolov3.weights`, `yolov3.cfg`, and `coco.names` are in the `ai_auto_categorizer` directory.

3. **Image Download Errors**: Check that the Cloudinary URLs are accessible and valid.

4. **No Matches Found**: Verify that there are lost items in the database with images.

### Logs

Check the logs for detailed information about the matching process:

- Java logs: Standard Spring Boot logs
- Python logs: The enhanced matcher creates a log file in the `ai_auto_categorizer` directory

## Future Improvements

1. **Direct Image Comparison**: Add functionality to directly compare two specific images.

2. **More Advanced Features**: Incorporate deep learning-based image similarity.

3. **Performance Optimization**: Cache results and optimize the matching algorithm for larger datasets.

4. **User Feedback Loop**: Use user feedback to improve the matching algorithm over time.
