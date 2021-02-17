package com.samsung.itschool.surfaceviewagain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    Resources resources = getResources();
    int color = Color.WHITE;
    boolean drag = false;
    float dragX = 0;
    float dragY = 0;
    int rectCol1 = resources.getColor(R.color.rect1,  null);
    int rectCol2 = resources.getColor(R.color.rect2,  null);
    int boardCol = resources.getColor(R.color.board,  null);
    int side = 200;
    float x = 400, y = 400;
    Rect centerRect = new Rect();
    Paint p3 = new Paint();
    DrawThread thread;
    Random r = new Random();
    int dx = 20, dy = 10, rad = 40;
    int width, height; // ширина и высота канвы
    public void changeColor() {
        color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
    }
    class DrawThread extends Thread {
        int color1 = resources.getColor(R.color.ball1,  null);
        boolean runFlag = true;
        float x1 = 300, y1 = 300;
        float x2 = 100, y2 = 200;
        Paint p1 = new Paint();
        Paint p2 = new Paint();
        Paint b = new Paint();
        boolean touch = false;

        public DrawThread(SurfaceHolder holder){
            this.holder = holder;
        }
        SurfaceHolder holder;

        @Override

        public void run() {
            super.run();
            p1.setColor(color1);
            p2.setColor(Color.MAGENTA);
            p3.setColor(rectCol1);
            b.setColor(boardCol);
            b.setStrokeWidth(50);

            while (runFlag){
                Canvas c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(color);
                    c.drawRect(x, y, x + side, y + side, p3);
                    width = c.getWidth();
                    height = c.getHeight();

                    c.drawLine(0,0,width,0,b);
                    c.drawLine(0,0,0,height ,b);
                    c.drawLine(0,height ,width,height ,b);
                    c.drawLine(width,0,width,height ,b);

                    if (x1 >= width - 50) {
                        x1 -= dx;
                    }
                    else if(x2 >= width - 50 ) {
                        x2 -= dx;
                    }
                    else if (y1 >= height - 50) {
                        y1 -= dy;
                    }
                    else if (y2 >= height - 50 ) {
                        y2 -= dy;
                    }
                    else if (x1 < 50){
                        x1 += dx;
                    }
                    else if (x2 < 50){
                        x2 += dy;
                    }else if (y1 < 50) {
                        y1 += dy;
                    }
                    else if (y2 < 50) {
                        y2 += dy;
                    }
                    else if (x1 == x2 || y1 == y2) {
                        x1 -= dx;
                        x2 += dx;
                        y1 += dy;
                        y2 -= dy;
                    }
                    else if ((x1 >= centerRect.left && x1 <= centerRect.right) && (y1 >= centerRect.top && y1 <= centerRect.bottom) ) {
                        x1 -= dx;
                        y1 += dy;

                        p1.setColor(Color.rgb( 67 , 178 , 21 ));
                    }
                    else if ((x2 >= centerRect.left && x2 <= centerRect.right) && (y2 >= centerRect.top && y2 <= centerRect.bottom) ) {
                        x2 -= dx;
                        y2 += dy;

                        p2.setColor(Color.rgb( 145 , 134 , 11 ));
                    }
                    else {
                        x1+= dx;
                        y1+= dy;

                        x2 += dx;
                        y2 += dy;
                    }
                    c.drawCircle(x1,y1,rad,p1);
                    c.drawCircle(x2,y2,rad,p2);
                    c.drawRect(centerRect, p3);

                    holder.unlockCanvasAndPost(c);

                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e){
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // координаты Touch-события
        float evX = event.getX();
        float evY = event.getY();

        switch (event.getAction()) {
            // касание началось
            case MotionEvent.ACTION_DOWN:
                p3.setColor(rectCol2);
                // если касание было начато в пределах квадрата
                if (evX >= x && evX <= x + side && evY >= y && evY <= y + side) {
                    // включаем режим перетаскивания
                    drag = true;
                    // разница между левым верхним углом квадрата и точкой касания
                    dragX = evX - x;
                    dragY = evY - y;
                }
                break;
            // тащим
            case MotionEvent.ACTION_MOVE:
                // если режим перетаскивания включен
                if (drag) {
                    // определеяем новые координаты для рисования
                    x = evX - dragX;
                    y = evY - dragY;
                    // перерисовываю экран
                    invalidate();
                }
                break;
            // касание завершено
            case MotionEvent.ACTION_UP:
                // выключаем режим перетаскивания
                p3.setColor(rectCol1);
                drag = false;
                break;
        }
        return true;
    }

    public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;
        thread = new DrawThread(holder);
        thread.start();
        Log.d("mytag", "DrawThread is running");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        thread.runFlag = false;
        thread = new DrawThread(holder);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.runFlag = false;
    }
}
