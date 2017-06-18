package com.udit.dodger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

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

    public void saveScores(Context context, List<HighScores> score) {
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

    public void addScore(Context context, HighScores highScores) {
        List<HighScores> scores = getScores(context);
        if (scores == null)
            scores = new ArrayList<HighScores>();
        if(scores.size()<11) {
            scores.add(highScores);
            saveScores(context, scores);
        }
    }

    public ArrayList<HighScores> getScores(Context context) {
        SharedPreferences settings;
        List<HighScores> scores;

        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(SCORES)) {
            String jsonScores = settings.getString(SCORES, null);
            Gson gson = new Gson();
            HighScores[] highScores = gson.fromJson(jsonScores,
                    HighScores[].class);

            scores = Arrays.asList(highScores);
            scores = new ArrayList<HighScores>(scores);
        } else {
            return null;
        }
        return (ArrayList<HighScores>) scores;
    }

    class MyComparator1 implements Comparator<HighScores> {
        @Override
        public int compare(HighScores highScores, HighScores t1) {
            return compareValue(highScores.getScore(),t1.getScore());
        }

        public int compareValue(int x, int y) {
            return x > y ? -1
                    : x < y ? 1
                    : 0;
        }
    }

    class MyComparator2 implements Comparator<HighScores> {
        @Override
        public int compare(HighScores highScores, HighScores t1) {
            return compareValue(highScores, t1);
        }

        public int compareValue(HighScores highScores, HighScores t1) {
            return highScores.getScore() == t1.getScore() ? (highScores.getTime()<t1.getTime() ? -1 : highScores.getTime()>t1.getScore() ? 1 : 0): 0;
        }
    }
}