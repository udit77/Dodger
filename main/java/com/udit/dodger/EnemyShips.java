package com.udit.dodger;

import android.graphics.Bitmap;

public class EnemyShips {
    int idxX;
    int idxY;
    Bitmap ship;
    int speed;
    String type;

    public EnemyShips(Bitmap ship, int idxX, int idxY, int speed, String type) {
        this.ship = ship;
        this.idxX = idxX;
        this.idxY = idxY;
        this.speed = speed;
        this.type = type;
    }

    public Bitmap getShip() {
        return this.ship;
    }

    public void setShip(Bitmap ship) {
        this.ship = ship;
    }

    public int getIdxX() {
        return this.idxX;
    }

    public void setIdxX(int idxX) {
        this.idxX = idxX;
    }

    public int getIdxY() {
        return this.idxY;
    }

    public void setIdxY(int idxY) {
        this.idxY = idxY;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
