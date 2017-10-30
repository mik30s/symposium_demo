package edu.tarleton.mike168m.lidarmapping;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


public class LidarPlotView extends SurfaceView implements SurfaceHolder.Callback {
    private Context ctx;
    private DrawThread thread;
    ArrayList<Lidar2DPoint> points = new ArrayList<>();

    private class Lidar2DPoint {
        int range;
        int x, y;

    }

    private class DrawThread extends Thread {
        SurfaceHolder surfaceHolder;
        LidarPlotView view;

        public DrawThread(SurfaceHolder holder, LidarPlotView view) {
            this.surfaceHolder = holder;
            this.view = view;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            while (true) {     //When setRunning(false) occurs, _run is
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        postInvalidate();
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public LidarPlotView(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        getHolder().addCallback(this);
    }

    void addPoint() {
        Lidar2DPoint point = new Lidar2DPoint();
        point.range = 0;
        point.x = 500;
        point.y = 400;
        points.add(point);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //do drawing stuff here.
        Lidar2DPoint p = points.get(points.size() - 1);
        canvas.drawRect(new Rect(p.x, p.y, p.x+50, p.y+50), new Paint());
        canvas.drawARGB(255,255,255,255);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()
        thread = new DrawThread(getHolder(), this); //Start the thread that
        thread.start();                              //onDraw()
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {}
    }
}
