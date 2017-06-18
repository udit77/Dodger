package com.udit.dodger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DodgerSplash extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dodger_splash);
        ImageView iv_background = (ImageView) findViewById(R.id.background);
        iv_background.setImageResource(R.drawable.image2);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ImageView appName = (ImageView)findViewById(R.id.appNameImage);
        appName.setImageResource(R.drawable.space_dodger);

        int appNameHeight = appName.getHeight();

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.mainLayout);
        ViewGroup.LayoutParams param = linearLayout.getLayoutParams();
        param.width = width-appNameHeight;
        linearLayout.setLayoutParams(param);

        ImageView enemyView1 = (ImageView) findViewById(R.id.enemyView1);
        ImageView enemyView2 = (ImageView) findViewById(R.id.enemyView2);

        //enemyView1.setPadding(0,width/16,0,0);
        //enemyView2.setPadding(0,0,0,width/16);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(DodgerSplash.this, Home.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}