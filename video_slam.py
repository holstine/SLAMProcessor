import cv2
import numpy as np
import argparse
import matplotlib.pyplot as plt

class VideoSLAM:
    def __init__(self, video_path):
        self.cap = cv2.VideoCapture(video_path)
        if not self.cap.isOpened():
            raise IOError(f"Cannot open video: {video_path}")
        self.orb = cv2.ORB_create(2000)
        self.prev_pts = None
        self.prev_gray = None
        self.feature_thresh = 100
        self.cur_pose = np.eye(3)
        self.trajectory = [(0.0, 0.0)]

    def process(self):
        ret, frame = self.cap.read()
        if not ret:
            raise ValueError("Unable to read first frame from video")
        gray_prev = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        kp = self.orb.detect(gray_prev, None)
        self.prev_pts = np.array([k.pt for k in kp], dtype=np.float32).reshape(-1, 1, 2)
        self.prev_gray = gray_prev

        while True:
            ret, frame = self.cap.read()
            if not ret:
                break
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

            if self.prev_pts is not None and len(self.prev_pts) > 0:
                next_pts, status, _ = cv2.calcOpticalFlowPyrLK(self.prev_gray, gray, self.prev_pts, None)
                status = status.reshape(-1)
                good_prev = self.prev_pts[status == 1].reshape(-1, 2)
                good_next = next_pts[status == 1].reshape(-1, 2)
                matches = list(zip(good_next, good_prev))
                if len(matches) >= 6:
                    src = np.float32([m[0] for m in matches])
                    dst = np.float32([m[1] for m in matches])
                    affine, inliers = cv2.estimateAffinePartial2D(src, dst)
                    if affine is not None:
                        transform = np.eye(3)
                        transform[:2, :] = affine
                        self.cur_pose = self.cur_pose @ np.linalg.inv(transform)
                        x, y = self.cur_pose[0, 2], self.cur_pose[1, 2]
                        self.trajectory.append((x, y))
                self.prev_pts = good_next.reshape(-1, 1, 2)
            else:
                self.prev_pts = np.empty((0, 1, 2), dtype=np.float32)

            if self.prev_pts.shape[0] < self.feature_thresh:
                kp = self.orb.detect(gray, None)
                self.prev_pts = np.array([k.pt for k in kp], dtype=np.float32).reshape(-1, 1, 2)

            self.prev_gray = gray

        self.cap.release()
        return self.trajectory


def main():
    parser = argparse.ArgumentParser(description="Simple SLAM-like algorithm to map an area from a video")
    parser.add_argument('video', help='Path to the input video file')
    args = parser.parse_args()
    slam = VideoSLAM(args.video)
    traj = slam.process()
    if len(traj) > 1:
        xs, ys = zip(*traj)
        plt.figure()
        plt.plot(xs, ys, marker='o')
        plt.title('Estimated Trajectory')
        plt.gca().invert_yaxis()
        plt.axis('equal')
        plt.xlabel('X')
        plt.ylabel('Y')
        plt.show()
    for idx, (x, y) in enumerate(traj):
        print(f"{idx}: {x:.2f}, {y:.2f}")

if __name__ == '__main__':
    main()
