package cass.oli.pong;

import android.graphics.Canvas;

public abstract class GameObject {
    public float x, y;
    public float velX, velY;
    public float mass;
    public Shape shape;
    public int id;

    Collision earliestCollisionResponse = new Collision();

    protected Collision temp = new Collision();

    protected Collision me = new Collision();
    protected Collision other = new Collision();

    public GameObject(float x, float y, Shape shape, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.shape = shape;
    }

    public abstract void intersect(BoxContainer box, float timeLimit);
    public abstract void intersect(GameObject object, float timeLimit);
    public abstract void update(float time);
    public abstract void render(Canvas canvas);
}
