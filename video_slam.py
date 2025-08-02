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
        self.bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)
        self.prev_kp = None
        self.prev_desc = None
        self.cur_pose = np.eye(3)
        self.trajectory = [(0.0, 0.0)]

    def process(self):
        ret, frame = self.cap.read()
        if not ret:
            raise ValueError("Unable to read first frame from video")
        gray_prev = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        self.prev_kp, self.prev_desc = self.orb.detectAndCompute(gray_prev, None)

        while True:
            ret, frame = self.cap.read()
            if not ret:
                break
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            kp, desc = self.orb.detectAndCompute(gray, None)
            if desc is None or self.prev_desc is None:
                self.prev_kp, self.prev_desc = kp, desc
                continue

            matches = self.bf.match(desc, self.prev_desc)
            matches = sorted(matches, key=lambda x: x.distance)[:150]
            if len(matches) < 6:
                self.prev_kp, self.prev_desc = kp, desc
                continue

            src = np.float32([kp[m.queryIdx].pt for m in matches])
            dst = np.float32([self.prev_kp[m.trainIdx].pt for m in matches])
            affine, inliers = cv2.estimateAffinePartial2D(src, dst)
            if affine is not None:
                transform = np.eye(3)
                transform[:2, :] = affine
                self.cur_pose = self.cur_pose @ np.linalg.inv(transform)
                x, y = self.cur_pose[0, 2], self.cur_pose[1, 2]
                self.trajectory.append((x, y))

            self.prev_kp, self.prev_desc = kp, desc

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
