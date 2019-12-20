package cass.oli.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Ball extends GameObject { //coordinates are centre of ball (point particle)
    public Paint paint = new Paint();
    public final int radius;

    private static final int MAX_SPEED = 30;
    private static final int MAX_INIT_SPEED = 20;
    private static final int MAX_RADIUS = 100;
    private static final int MIN_RADIUS = 25;

    public Ball(int x, int y, int objId) {
        super(x, y, Shape.Circle, objId);
        Random random = new Random();
        //Bright Colours (possible better system)
        paint.setARGB(255, random.nextInt(128) + 128, random.nextInt(128) + 128, random.nextInt(128) + 128);

        velX = random.nextInt(MAX_INIT_SPEED*2) - MAX_INIT_SPEED;
        velY = random.nextInt(MAX_INIT_SPEED) - MAX_INIT_SPEED;
        radius = random.nextInt(MAX_RADIUS-MIN_RADIUS)+ MIN_RADIUS;
        mass = radius * radius; //*PI (Constant so doesn't matter)
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
   }

    @Override
    public void intersect(BoxContainer box, float timeLimit) {
        CollisionPhysics.pointIntersectsRectangleOuter(
                this.x, this.y, this.velX, this.velY, this.radius,
                box.x1, box.y1, box.x2, box.y2,
                timeLimit, temp);
        if(temp.t < earliestCollisionResponse.t) earliestCollisionResponse.copy(temp);
    }

    @Override
    public void intersect(GameObject object, float timeLimit) {
        switch(object.shape) {
            case Circle:
                Ball ball = (Ball) object;
                CollisionPhysics.pointIntersectsMovingPoint(
                        this.x, this.y, this.velX, this.velY, this.radius,
                        ball.x, ball.y, ball.velX, ball.velY, ball.radius,
                        timeLimit, me, other);
                if (other.t < ball.earliestCollisionResponse.t) ball.earliestCollisionResponse.copy(other);
                if (me.t < this.earliestCollisionResponse.t)    this.earliestCollisionResponse.copy(me);
                break;
            default:
                break;

        }
    }

    @Override
    public void update(float time) {
        if(velX > MAX_SPEED) velX = MAX_SPEED;
        if(velY > MAX_SPEED) velY = MAX_SPEED;

        if(earliestCollisionResponse.t <= time) {
            this.x = earliestCollisionResponse.getNewX(this.x, this.velX);
            this.y = earliestCollisionResponse.getNewY(this.y, this.velY);
            this.velX = earliestCollisionResponse.nVelX;
            this.velY = earliestCollisionResponse.nVelY;
        }else {
            this.x += this.velX * time;
            this.y += this.velY * time;
        }
        earliestCollisionResponse.reset();
    }

    public void print() {
        Log.d("PONG", "Ball | Id: " + id + " | x = " + x + " | y = " + y + " | velX = " + velX + " | velY = " + velY);
    }

    public boolean ballCollision(Ball ball) {
        float xDist = ball.x - this.x;
        float yDist = ball.y - this.y;
        float dist = xDist * xDist + yDist * yDist;
        return (dist < (ball.radius + this.radius)*(ball.radius + this.radius));
    }
}
