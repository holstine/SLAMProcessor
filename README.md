# SLAMProcessor

This repository contains a simple Java based SLAM processor. This update adds a Python script that performs a simple SLAM-like visual odometry on an input video.

## Usage

The Python script `video_slam.py` requires OpenCV and Matplotlib. Install dependencies:

```bash
pip install opencv-python matplotlib
```

Run the script with a video file path:

```bash
python3 video_slam.py path/to/video.mp4
```

The script prints estimated (x, y) positions for each processed frame and displays a 2D trajectory plot.
