package com.samsung.itschool.surfaceviewagain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    MediaPlayer mediaPlayer;
    SurfaceHolder holder;
    Resources resources = getResources();
    int color = Color.WHITE;
    boolean drag = false;
    float dragX = 0;
    float dragY = 0;

    int left, top, right, bottom;

    float x1 = 300, y1 = 300;
    float x2 = 100, y2 = 200;

    ArrayList<Integer> colors = new ArrayList<Integer>(Arrays.asList(
            R.color.ball1, R.color.ball2, R.color.ball3, R.color.ball4
    ));

    int rectCol1 = resources.getColor(R.color.rect1,  null);
    int rectCol2 = resources.getColor(R.color.rect2,  null);
    int boardCol = resources.getColor(R.color.board,  null);
    int side = 200;
    int x = 400, y = 400;
    Rect centerRect = new Rect();
    Paint p3 = new Paint();
    DrawThread thread;
    Random r = new Random();
    int width, height; // ширина и высота канвы
    class DrawThread extends Thread {
        boolean runFlag = true;
        Paint p1 = new Paint();
        Paint p2 = new Paint();
        Paint b = new Paint();
        Paint win = new Paint();

        public DrawThread(SurfaceHolder holder){
            this.holder = holder;
        }
        SurfaceHolder holder;

        @Override

        public void run() {
            super.run();
            p1.setColor(getResources().getColor(R.color.ball1));
            p2.setColor(getResources().getColor(R.color.ball2));
            p3.setColor(rectCol1);
            b.setColor(boardCol);
            b.setStrokeWidth(50);

            float dx1 = 20, dy1 = 10;
            float dx2 = -30, dy2 = 25;

            int distance = 50;
            float rad = 30;

            left = 400;
            top = 400;
            right = 600;
            bottom = 600;

            x1+= r.nextFloat() * distance - 10;
            y1+= r.nextFloat() * distance - 10;

            x2 += r.nextFloat() * distance - 10;
            y2 += r.nextFloat() * distance - 10;

            while (runFlag){
                Canvas c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(color);
                    centerRect.set(x, y, x + side, y + side);
                    width = c.getWidth();
                    height = c.getHeight();

                    c.drawLine(0,0,width,0,b);
                    c.drawLine(0,0,0,height ,b);
                    c.drawLine(0,height ,width,height ,b);
                    c.drawLine(width,0,width,height ,b);

                    if (x1 >= width - 50 - rad) {
                        dx1 = -dx1;
                        makeSound();
                    }
                    else if(x2 >= width - 50-rad){
                        makeSound();
                        dx2 = -dx2;
                    } else if (y1 >= height - 50 - rad) {
                        makeSound();
                        dy1 = -dy1;
                    } else if(y2 >= height - 50 - rad){
                        makeSound();
                        dy2 = -dy2;
                    } else if (x1 < 50 + rad){
                        makeSound();
                        dx1 = -dx1;
                    } else if(x2 < 50 + rad){
                        makeSound();
                        dx2 = -dx2;
                    }else if (y1 < 50 + rad) {
                        makeSound();
                        dy1 = -dy1;
                    } else if(y2 < 50 + rad){
                        makeSound();
                        dy2 = -dy2;
                    }
                    else if (y2 == y1) {
                        makeSound();
                        dy1 = -dy1;
                        dy2 = -dy2;
                    }
                    else if (x2 == x1) {
                        makeSound();
                        dx1 = -dx1;
                        dx2 = -dx2;
                    }
                    else if ((x1 >= centerRect.left-25 && x1 <= centerRect.right-25) && (y1 >= centerRect.top-25 && y1 <= centerRect.bottom-25) ) {
                        makeSound();
                        dx1 = -dx1;
                        dy1 = -dy1;
                        int random = (int) (r.nextFloat() * colors.size());
                        int color = colors.get(random);
                        p1.setColor(getResources().getColor(color));
                    }
                    else if ((x2 >= centerRect.left-25 && x2 <= centerRect.right-25) && (y2 >= centerRect.top-25 && y2 <= centerRect.bottom-25)) {
                        makeSound();
                        dx2 = -dx2;
                        dy2 = -dy2;
                        int random = (int) (r.nextFloat() * colors.size());
                        int color = colors.get(random);
                        p2.setColor(getResources().getColor(color));
                    }
                    x1 += dx1;
                    y1 += dy1;

                    x2 += dx2;
                    y2 += dy2;

                    c.drawCircle(x1,y1,rad,p1);
                    c.drawCircle(x2,y2,rad,p2);
                    c.drawRect(centerRect, p3);

                    if (p1.getColor() == p2.getColor()){
                        win.setColor(getResources().getColor(R.color.colorPrimaryDark));
                        win.setTextSize(200);
                        c.drawText("You win!", 200, 800, win);
                        runFlag=false;
                    }
                }

                holder.unlockCanvasAndPost(c);

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
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
                    x = (int) (evX - dragX);
                    y = (int) (evY - dragY);
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
        mediaPlayer = MediaPlayer.create(context, R.raw.sound1);
        getHolder().addCallback(this);
        thread = new DrawThread(getHolder());
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

    public void makeSound() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.sound1);
        mediaPlayer.start();
    }
}
