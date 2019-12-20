package cass.oli.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.LinkedList;

public class Pong extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;

    public int WIDTH, HEIGHT;
    public BoxContainer box;
    private int objID = 0;
    public static final int UPDATE_RATE = 60; //FPS
    public static final float EPSILON_TIME = 1e-2f; //Threshold for zero time

    Paint paint;

    ArrayList<GameObject> objects = new ArrayList<GameObject>();
    ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
    ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();

    public Pong(Context context){
        super(context);

        this.setFocusable(true);
        this.getHolder().addCallback(this);
        this.gameThread = new GameThread(this);
        this.gameThread.start();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        paint.setTextSize(72);
    }

    public void update(){
        //Synchronises adding and removing objects
        for(GameObject object : addObjects) {
            boolean add = true;
            switch(object.shape) {
                case Circle:
                    Ball nBall = (Ball) object;
                    if(nBall.x - nBall.radius < 0 || nBall.x + nBall.radius > WIDTH || nBall.y - nBall.radius < 0 || nBall.y + nBall.radius > HEIGHT) {
                        add = false;
                        break;
                    }
                    for(GameObject exist : objects) {
                        switch(exist.shape) {
                            case Circle:
                                Ball ball = (Ball) exist;
                                if(ball.ballCollision(nBall)) add = false;
                                break;
                            default:
                                break;

                        }
                    }
                    break;
                default:
                    break;

            }

            if(add) objects.add(object);
        }
        addObjects.clear();


        for(GameObject object : removeObjects) {
            objects.remove(object);
        }
        removeObjects.clear();

        float timeLeft = 1.0f;

        do {
            float tMin = timeLeft;

            //Object-Object Collision
            for(int i = 0; i < objects.size(); i++) {
                for(int j = 0; j < objects.size(); j++) {
                    if(i < j) {
                        objects.get(i).intersect(objects.get(j), tMin);
                        if(objects.get(i).earliestCollisionResponse.t < tMin)
                            tMin = objects.get(i).earliestCollisionResponse.t;
                    }
                }
            }

            //Object-Wall Collision
            for(GameObject object : objects) {
                object.intersect(box, timeLeft);
                if(object.earliestCollisionResponse.t < tMin) {
                    tMin = object.earliestCollisionResponse.t;
                }
            }
            //Move
            for(GameObject object : objects) object.update(tMin);
            timeLeft -= tMin;
        }while(timeLeft > EPSILON_TIME);
    }
    public void draw(Canvas canvas){
        super.draw(canvas);
        for (GameObject object : objects) object.render(canvas);
        if(objects.size() == 0){
            canvas.drawText("Tap Anywhere To Begin...", 125, getHeight() - 100, paint);
        }

        if(gameThread.running) {
            canvas.drawRect(this.getWidth() - 200, 50, this.getWidth() - 160, 160, paint);
            canvas.drawRect(this.getWidth() - 130, 50, this.getWidth() - 90, 160, paint);
        }else{
            Path path = new Path();
            path.moveTo(this.getWidth() - 200, 50);
            path.lineTo(this.getWidth() - 90, 105);
            path.lineTo(this.getWidth() - 200, 160);
            path.lineTo(this.getWidth() - 200, 50);
            path.close();

            canvas.drawPath(path, paint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        gameThread.setHolder(holder);
        this.gameThread.setRunning(true);
        this.WIDTH = this.getWidth();
        this.HEIGHT = this.getHeight();
        this.box = new BoxContainer(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        gameThread.setHolder(holder);
        this.gameThread.setRunning(true);
        this.WIDTH = this.getWidth();
        this.HEIGHT = this.getHeight();
        this.box = new BoxContainer(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        gameThread.setRunning(false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) tap(event);
        return false;
    }

    public void tap(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();

        //Pause Button
        if(x > WIDTH - 200 && x < WIDTH - 90 && y > 50 && y < 160) {
            gameThread.running = !gameThread.running;
            return;
        }

        //if ball pressed remove
        if(gameThread.running){
            //Remove Ball
            for(GameObject object : objects){
                if(object.shape == Shape.Circle) {
                    Ball ball = (Ball) object;
                    double xDist = x - ball.x;
                    double yDist = y - ball.y;
                    double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                    if(dist < ball.radius) {
                        //ball.print();
                        removeBall(ball);
                        return;
                    }
                }
            }
        }

        //if not anything else add ball if game running
        if(gameThread.running) {
            //Add Ball
            addBall(new Ball(x, y, objID));
            Log.d("PONG", "NEW BALL");
            objID++;
        }
    }

    public void addBall(Ball ball) {
        addObjects.add(ball);
    }
    public void removeBall(Ball ball) {
        removeObjects.add(ball);
    }
}
