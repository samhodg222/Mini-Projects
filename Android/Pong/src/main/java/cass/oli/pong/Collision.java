package cass.oli.pong;
/*
 * Source:
 * https://www3.ntu.edu.sg/home/ehchua/programming/java/J8a_GameIntro-BouncingBalls.html
 */

public class Collision{
    public float t;
    private static final float T_EPSILON = 0.005f;

    public float nVelX, nVelY;

    public Collision() {
        reset();
    }

    public void reset() {
        this.t = Float.MAX_VALUE;
    }

    public void copy(Collision col) {
        this.t = col.t;
        this.nVelX = col.nVelX;
        this.nVelY = col.nVelY;
    }

    public float getNewX(float currentX, float velX) {
        if(t > T_EPSILON) {
            return (float)(currentX + velX * (t - T_EPSILON));
        }else return currentX;
    }

    public float getNewY(float currentY, float velY) {
        if(t > T_EPSILON) {
            return (float)(currentY + velY * (t - T_EPSILON));
        } else return currentY;
    }

    public double getImpactX(float currentX, float speedX) {
        return currentX + speedX * t;
    }

    public double getImpactY(float currentY, float speedY) {
        return currentY + speedY * t;
    }
}
