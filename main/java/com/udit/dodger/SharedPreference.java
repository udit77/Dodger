package com.udit.dodger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 16-Jun-17.
 */

public class SharedPreference {

    public static final String PREFS_NAME = "HIGH_SCORES";
    public static final String SCORES = "High_Score";

    public SharedPreference() {
        super();
    }

    public void saveScores(Context context, List<HighScore> score) {
        SharedPreferences scoresPref;
        SharedPreferences.Editor editor;
        scoresPref = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);
        editor = scoresPref.edit();

        Collections.sort(score, new MyComparator1());
        Collections.sort(score, new MyComparator2());

        if(score.size()==11){
            score.remove(10);
        }

        Gson gson = new Gson();
        String jsonScores = gson.toJson(score);
        editor.putString(SCORES, jsonScores);
        Log.d("------------", "" + jsonScores);

        editor.commit();
    }

    public void addScore(Context context, HighScore highScore) {
        List<HighScore> scores = getScores(context);
        if (scores == null)
            scores = new ArrayList<HighScore>();
        if(scores.size()<11) {
            scores.add(highScore);
            saveScores(context, scores);
        }
    }

    public ArrayList<HighScore> getScores(Context context) {
        SharedPreferences settings;
        List<HighScore> scores;

        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(SCORES)) {
            String jsonScores = settings.getString(SCORES, null);
            Gson gson = new Gson();
            HighScore[] highScores = gson.fromJson(jsonScores,
                    HighScore[].class);

            scores = Arrays.asList(highScores);
            scores = new ArrayList<HighScore>(scores);
        } else {
            return null;
        }
        return (ArrayList<HighScore>) scores;
    }

    class MyComparator1 implements Comparator<HighScore> {
        @Override
        public int compare(HighScore highScore, HighScore t1) {
            return compareValue(highScore.getScore(),t1.getScore());
        }

        public int compareValue(int x, int y) {
            return x > y ? -1
                    : x < y ? 1
                    : 0;
        }
    }

    class MyComparator2 implements Comparator<HighScore> {
        @Override
        public int compare(HighScore highScore, HighScore t1) {
            return compareValue(highScore, t1);
        }

        public int compareValue(HighScore highScore,HighScore t1) {
            return highScore.getScore() == t1.getScore() ? (highScore.getTime()<t1.getTime() ? -1 : highScore.getTime()>t1.getScore() ? 1 : 0): 0;
        }
    }
}