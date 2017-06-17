package com.udit.dodger;

/**
 * Created by User on 16-Jun-17.
 */

public class HighScore {
    long time;
    int score;

    public HighScore(){

    }

    public HighScore(long time, int score) {
        this.time = time;
        this.score = score;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}