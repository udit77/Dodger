package com.udit.dodger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Home extends Activity {

    boolean backButtonPressed = false;
    long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ImageView iv_background = (ImageView) findViewById(R.id.background);
        iv_background.setImageResource(R.drawable.image2);


        Button play = (Button)findViewById(R.id.play);
        Button score = (Button)findViewById(R.id.score);
        Button tutorial = (Button)findViewById(R.id.tutorial);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScores();
            }
        });

        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });
    }

    public void startGame(){
        Intent intent = new Intent(this,Game.class);
        startActivity(intent);
        finish();
    }

    public void showScores() {
        Intent intent = new Intent(this,Score.class);
        startActivity(intent);
        finish();
    }

    public void showTutorial() {
        Intent intent = new Intent(this,Tutorial.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onBackPressed(){
        if(backButtonPressed){
            long doublePressedTime = System.currentTimeMillis();
            if((doublePressedTime-backPressedTime)/1000 <=4){
                super.finish();
            }
            else{
                backButtonPressed = false;
                backPressedTime = 0;
                Toast toast = Toast.makeText(getApplicationContext(), "Press again to close dodger", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM , 0, 0);
                toast.show();
            }
        }else {
            Toast toast = Toast.makeText(getApplicationContext(), "Press again to close dodger", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM , 0, 0);
            toast.show();
        }
        backButtonPressed = true;
        backPressedTime = System.currentTimeMillis();
        return;
    }
}
