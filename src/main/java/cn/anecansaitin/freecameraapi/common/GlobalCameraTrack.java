package cn.anecansaitin.freecameraapi.common;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;

/// 全局相机轨迹
public class GlobalCameraTrack {
    private final ArrayList<CameraPoint> points;
    private final IntArrayList timeLine;

    public GlobalCameraTrack() {
        points = new ArrayList<>();
        timeLine = new IntArrayList();
    }

    /// 把点加入到指定时间
    ///
    /// 相同时间点进行覆盖
    public void add(CameraPoint point, int time) {
        if (timeLine.isEmpty()) {
            timeLine.add(time);
            points.add(point);
        } else {
            int left = 0;
            int right = timeLine.size() - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                int midVal = timeLine.getInt(mid);

                if (midVal < time) {
                    left = mid + 1;
                } else if (midVal > time) {
                    right = mid - 1;
                } else {
                    left = mid; // 找到了相同值的位置
                    right = -1; // -1表示已经存在这个值
                    break;
                }
            }

            if (right == -1) {
                points.set(left, point);
            } else {
                points.add(left, point);
                timeLine.add(left, time);
            }
        }
    }

    public void remove(int index) {
        points.remove(index);
        timeLine.removeInt(index);
    }

    public CameraPoint getPoint(int index) {
        return points.get(index);
    }

    public int getTime(int index) {
        return timeLine.getInt(index);
    }
}
