package cass.oli.pong;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread{
    private Pong pong;
    private SurfaceHolder surfaceHolder;
    public boolean running;

    public GameThread(Pong pong){
        this.pong = pong;
        running = false;
    }

    public void setHolder(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run(){
        while(true) {
            long beginTimeMillis, timeTakenMillis, timeLeftMillis;
            beginTimeMillis = System.currentTimeMillis();

            Canvas canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (canvas) {
                    this.pong.draw(canvas);
                    if(running) this.pong.update();
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
            timeLeftMillis = 1000L / Pong.UPDATE_RATE - timeTakenMillis;
            if(timeLeftMillis < 5) timeLeftMillis = 5;

            try {
                Thread.sleep(timeLeftMillis);
            }catch(InterruptedException e) {}
        }
    }
}
