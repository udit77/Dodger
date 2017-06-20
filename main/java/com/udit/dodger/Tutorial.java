package com.udit.dodger;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ImageView iv_background = (ImageView) findViewById(R.id.background);
        iv_background.setImageResource(R.drawable.background);

        TextView tv = (TextView)findViewById(R.id.tutorialView);
        ViewGroup.LayoutParams param = tv.getLayoutParams();
        param.width = width/2+100;
        tv.setLayoutParams(param);
        tv.setText("* Save your Warrior Ship from Enemy War Ships."
                + System.getProperty("line.separator")
                +System.getProperty("line.separator")
                +"* Tap at bottom right of your screen to move your ship up and bottom left to move down."
                +System.getProperty("line.separator")
                +System.getProperty("line.separator")
                +"* Do take care of dangerous fire balls."
                +System.getProperty("line.separator")
                +System.getProperty("line.separator")
                +"   Happy Space Dodger :)  - Team@gfers."
        );
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, Home.class);
        startActivity(mainIntent);
        finish();
    }
}
