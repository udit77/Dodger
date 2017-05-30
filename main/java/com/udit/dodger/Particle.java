package com.udit.dodger;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import java.util.Random;

public class Particle {
    public static final int DEFAULT_LIFETIME = 200;
    public static final int MAX_DIMENSION = 5;
    public static final int MAX_SPEED = 10;
    public static final int STATE_ALIVE = 0;
    public static final int STATE_DEAD = 1;
    private int age;
    private int color;
    private float height;
    private int lifetime;
    private Paint paint;
    private int state;
    private float width;
    private float f5x;
    private double xv;
    private float f6y;
    private double yv;

    public Particle(int x, int y) {
        this.f5x = (float) x;
        this.f6y = (float) y;
        this.state = STATE_ALIVE;
        this.width = (float) rndInt(STATE_DEAD, MAX_DIMENSION);
        this.height = this.width;
        this.lifetime = DEFAULT_LIFETIME;
        this.age = STATE_ALIVE;
        this.xv = rndDbl(STATE_ALIVE, 20) - 10.0d;
        this.yv = rndDbl(STATE_ALIVE, 20) - 10.0d;
        if ((this.xv * this.xv) + (this.yv * this.yv) > 100.0d) {
            this.xv *= 0.7d;
            this.yv *= 0.7d;
        }
        this.color = Color.argb(MotionEventCompat.ACTION_MASK, rndInt(STATE_ALIVE, MotionEventCompat.ACTION_MASK), rndInt(STATE_ALIVE, MotionEventCompat.ACTION_MASK), rndInt(STATE_ALIVE, MotionEventCompat.ACTION_MASK));
        this.paint = new Paint(this.color);
    }

    public void update() {
        if (this.state != STATE_DEAD) {
            this.f5x = (float) (((double) this.f5x) + this.xv);
            this.f6y = (float) (((double) this.f6y) + this.yv);
            int a = (this.color >>> 24) - 2;
            if (a <= 0) {
                this.state = STATE_DEAD;
            } else {
                this.color = (this.color & ViewCompat.MEASURED_SIZE_MASK) + (a << 24);
                this.paint.setAlpha(a);
                this.age += STATE_DEAD;
            }
            if (this.age >= this.lifetime) {
                this.state = STATE_DEAD;
            }
        }
    }

    public double rndDbl(int rangeMin, int rangeMax) {
        return ((double) rangeMin) + (((double) (rangeMax - rangeMin)) * new Random().nextDouble());
    }

    public int rndInt(int rangeMin, int rangeMax) {
        return rangeMin + ((rangeMax - rangeMin) * new Random().nextInt());
    }
}
