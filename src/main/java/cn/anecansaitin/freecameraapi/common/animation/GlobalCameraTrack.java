package cn.anecansaitin.freecameraapi.common.animation;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.joml.Vector3f;

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
    public void add(int time, CameraPoint point) {
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
                updateBezier(left);
            } else {
                points.add(left, point);
                timeLine.add(left, time);
                updateBezier(left);
            }
        }
    }

    /// 更新控制点
    private void updateBezier(int index) {
        CameraPoint point = points.get(index);

        if (index > 0 && point.getType() == PointInterpolationType.BEZIER) {
            CameraPoint prePoint = points.get(index - 1);
            Vector3f c = new Vector3f(point.getPosition()).add(prePoint.getPosition()).mul(0.5f);
            prePoint.setRightBezierControl(c.x, c.y, c.z);
            point.setLeftBezierControl(c.x, c.y, c.z);
        }

        if (index < points.size() - 1) {
            CameraPoint nextPoint = points.get(index + 1);

            if (nextPoint.getType() != PointInterpolationType.BEZIER) {
                return;
            }

            Vector3f c = new Vector3f(point.getPosition()).add(nextPoint.getPosition()).mul(0.5f);
            nextPoint.setLeftBezierControl(c.x, c.y, c.z);
            point.setRightBezierControl(c.x, c.y, c.z);
        }
    }

    public void remove(int index) {
        if (index > 0 && index < points.size() - 1) {
            CameraPoint next = points.get(index + 1);;

            if (next.getType() == PointInterpolationType.BEZIER) {
                CameraPoint pre = points.get(index - 1);
                Vector3f mid = new Vector3f(pre.getPosition()).add(next.getPosition()).mul(0.5f);
                pre.setRightBezierControl(mid.x, mid.y, mid.z);
                next.setLeftBezierControl(mid.x, mid.y, mid.z);
            }
        }

        points.remove(index);
        timeLine.removeInt(index);
    }

    public CameraPoint getPoint(int index) {
        return points.get(index);
    }

    public int getTime(int index) {
        return timeLine.getInt(index);
    }

    public int getCount() {
        return points.size();
    }
}
