package com.udit.dodger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

import static com.udit.dodger.R.drawable.overlay;
import static com.udit.dodger.R.drawable.resume;

public class MainActivity extends Activity {
    int bgIndex;
    EnemyShips[] enemyShips;
    int fireGap;
    int initialFireIndex;
    MySurfaceView mySurfaceView;
    Vibrator v;
    int warrior_Y;
    int winHeight;
    int winWidth;
    int score =0,count=0,lifeCount=5,colliding_enemy=-1;
    static boolean flag=true;
    boolean gameThreadPaused;
    private Rect r = new Rect();
    boolean backButtonPressed = false;
    long backPressedTime = 0;
    boolean isGameOver = false;

    class MySurfaceView extends SurfaceView implements Runnable {
        Bitmap backgroundImage;
        Canvas canvas;
        volatile boolean running;
        SurfaceHolder surfaceHolder;
        Thread thread;
        Bitmap warrior;
        Bitmap life;
        Bitmap resume,restart,menu,overlay,gameOver,yourScore;
        int gameOver_x,gameOver_y;

        public MySurfaceView(Context context) {
            super(context);
            this.thread = null;
            this.running = false;
            this.surfaceHolder = getHolder();
            MainActivity.this.initWindow();
            this.warrior = BitmapFactory.decodeResource(getResources(), R.drawable.warrior);
            this.life = BitmapFactory.decodeResource(getResources(), R.drawable.life);
            this.resume = BitmapFactory.decodeResource(getResources(), R.drawable.resume);
            this.restart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
            this.menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
            this.gameOver = BitmapFactory.decodeResource(getResources(),R.drawable.game_over);
            this.yourScore = BitmapFactory.decodeResource(getResources(),R.drawable.your_score);
            this.overlay = getScaledImage(BitmapFactory.decodeResource(getResources(), R.drawable.overlay),0.6f);

            MainActivity.this.warrior_Y = (MainActivity.this.winHeight / 2) - (this.warrior.getHeight() / 2);
            initShips();
            this.backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
            this.backgroundImage = getScaledImage(this.backgroundImage);
            gameOver_x = (winWidth-gameOver.getWidth())/2;
            gameOver_y = 0;
        }

        public void onResumeMySurfaceView() {
            this.running = true;
            this.thread = new Thread(this);
            this.thread.start();
        }

        public void onPauseMySurfaceView() {
            boolean retry = true;
            this.running = false;
            while (retry) {
                try {
                    this.thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void run() {
            while (this.running) {
                if (this.surfaceHolder.getSurface().isValid()) {
                    this.canvas = this.surfaceHolder.lockCanvas();
                    createBackGround();
                    moveEnemyShip();
                    showLife();
                    showScore();
                    drawMenu();
                    drawGameOverMenu();
                    this.surfaceHolder.unlockCanvasAndPost(this.canvas);
                }
            }
        }

        public void drawGameOverMenu(){
            if(isGameOver){
                Paint paint = new Paint();
                if(gameOver_y<(winHeight-gameOver.getHeight())/2-50) {
                    gameOver_y=gameOver_y+3;
                    this.canvas.drawBitmap(this.gameOver, gameOver_x, gameOver_y , paint);
                }
                else{
                    this.canvas.drawBitmap(this.gameOver, gameOver_x, gameOver_y , paint);
                    drawYourScore();
                    drawGameEndMenu();
                }

            }else
                return;
        }

        public void drawGameEndMenu(){
            int offset = overlay.getWidth()/2;
            int initialX = (winWidth-overlay.getWidth())/2-restart.getWidth()/2;
            int x1 = initialX+offset/2;
            int x2 = x1+offset;
            int left,top,right,bottom;

            left = (winWidth-overlay.getWidth())/2;
            top = (winHeight-overlay.getHeight())/2;
            right = left+overlay.getWidth()/2;
            bottom = top+overlay.getHeight();

            int textY = bottom;
            int y = textY-(int)(restart.getHeight()*1.5);

            Paint paint = new Paint();
            paint.setColor(-1);
            paint.setStrokeWidth(2.0f);
            paint.setTextSize(25.0f);
            paint.setTextAlign(Paint.Align.LEFT);

            r.set(left,top,right,bottom);
            this.canvas.getClipBounds(r);

            String Restart = "Restart";
            String Menu = "Menu";

            paint.getTextBounds(Restart, 0, Restart.length(), r);
            this.canvas.drawBitmap(this.restart,x1,y, paint);
            this.canvas.drawBitmap(this.menu,x2,y, paint);

            this.canvas.drawText(Restart, x1-r.width()/2+restart.getWidth()/2, textY, paint);
            left=left+winWidth/2;
            paint.getTextBounds(Menu, 0, Menu.length(), r);
            this.canvas.drawText(Menu, x2-r.width()/2+restart.getWidth()/2, textY, paint);
        }

        public void drawYourScore(){
            int initialX = (winWidth-mySurfaceView.overlay.getWidth())/2-mySurfaceView.resume.getWidth()/2;
            int offset = mySurfaceView.overlay.getWidth()/2;
            int x1 = initialX+offset/2;

            Paint paint = new Paint();
            this.canvas.drawBitmap(this.yourScore,x1,(winHeight-gameOver.getHeight())/2 +80, paint);

            int score_x = x1+yourScore.getWidth()+40;
            int score_y = (winHeight+gameOver.getHeight())/2-40;
            paint.setColor(Color.parseColor("#71D57D"));
            paint.setStrokeWidth(2.0f);
            paint.setTextSize(40.0f);
            paint.setTextAlign(Paint.Align.RIGHT);
            this.canvas.drawText("  "+score,score_x,score_y, paint);
        }

        public void drawOverlay(){
            if(!gameThreadPaused)
                return;
            Paint paint = new Paint();
            this.canvas.drawBitmap(this.overlay,(winWidth-overlay.getWidth())/2, (winHeight-overlay.getHeight())/2, paint);
        }

        public void drawMenu(){
            if(!gameThreadPaused)
                return;
            int y = (winHeight)-(resume.getHeight()*2);
            int textY = winHeight-5;
            int offset = overlay.getWidth()/3;
            int initialX = (winWidth-overlay.getWidth())/2-resume.getWidth()/2;
            int x1 = initialX+offset/2;
            int x2 = x1+offset;
            int x3 = x2+offset;


            int left,top,right,bottom;

            left = (winWidth-overlay.getWidth())/2;
            top = (winHeight-overlay.getHeight())/2;
            right = left+overlay.getWidth()/3;
            bottom = top+overlay.getHeight();


            Paint paint = new Paint();
            paint.setColor(-1);
            paint.setStrokeWidth(2.0f);
            paint.setTextSize(25.0f);
            paint.setTextAlign(Paint.Align.LEFT);

            r.set(left,top,right,bottom);
            this.canvas.getClipBounds(r);

            String Resume = "Resume";
            String Restart = "Restart";
            String Menu = "Menu";

            paint.getTextBounds(Resume, 0, Resume.length(), r);
            this.canvas.drawBitmap(this.resume,x1,y, paint);
            this.canvas.drawBitmap(this.restart,x2,y, paint);
            this.canvas.drawBitmap(this.menu,x3,y, paint);

            this.canvas.drawText(Resume, x1-r.width()/2+resume.getWidth()/2, textY, paint);
            left=left+winWidth/3;
            paint.getTextBounds(Restart, 0, Restart.length(), r);
            this.canvas.drawText(Restart, x2-r.width()/2+restart.getWidth()/2, textY, paint);

            left=left+winWidth/3;
            paint.getTextBounds(Menu, 0, Menu.length(), r);
            this.canvas.drawText(Menu, x3-r.width()/2+menu.getWidth()/2, textY, paint);

        }

        public void showLife(){
            if(isGameOver)
                return;
            for(int i=0;i<lifeCount;i++){
                int x = 20+i*30;
                this.canvas.drawBitmap(this.life,(float)x,25.0f, new Paint());
            }
        }

        public void showScore(){
            if(isGameOver)
                return;
            Paint paint = new Paint();
            paint.setColor(-1);
            paint.setStrokeWidth(2.0f);
            paint.setTextSize(25.0f);
            paint.setTextAlign(Paint.Align.RIGHT);
            this.canvas.drawText("Score : "+score+" ", (float)winWidth-20, 50.0f, paint);
            count++;
            if(count%10==0)
                increaseScore(1);
        }

        public void increaseScore(int increment){
            if(gameThreadPaused)
                return;
            score+=increment;
        }
        public void createBackGround() {
            int imageWidth = Math.round((float) this.backgroundImage.getWidth());
            if (gameThreadPaused || isGameOver) {
                this.canvas.drawBitmap(this.backgroundImage, (float) MainActivity.this.bgIndex, 0.0f, new Paint());
                this.canvas.drawBitmap(this.backgroundImage, (float) (MainActivity.this.bgIndex - imageWidth), 0.0f, new Paint());
            } else {
                MainActivity.this.bgIndex++;
                if (MainActivity.this.bgIndex > imageWidth) {
                    MainActivity.this.bgIndex = 0;
                }
                this.canvas.drawBitmap(this.backgroundImage, (float) MainActivity.this.bgIndex, 0.0f, new Paint());
                this.canvas.drawBitmap(this.backgroundImage, (float) (MainActivity.this.bgIndex - imageWidth), 0.0f, new Paint());
            }
        }

        public void moveEnemyShip() {
            if(isGameOver){
            }
            else if(gameThreadPaused){
                try {
                    this.thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int i=0;
                while (i < MainActivity.this.enemyShips.length) {
                    this.canvas.drawBitmap(MainActivity.this.enemyShips[i].ship, MainActivity.this.enemyShips[i].getIdxX(), MainActivity.this.enemyShips[i].getIdxY(), new Paint());
                    i++;
                }
            }
            else{
            int i=0;
            while (i < MainActivity.this.enemyShips.length) {
                if (i != 0) {
                    MainActivity.this.enemyShips[i].idxX -= 6;
                    if (MainActivity.this.enemyShips[i].getIdxX()< (MainActivity.this.enemyShips[i].ship.getWidth() * -1) + 5) {
                        int nxtIdx = i == 1 ? 6 : i - 1;
                        if (i == 5) {
                            increaseScore(2);
                            MainActivity.this.enemyShips[i].setIdxX(MainActivity.this.enemyShips[i - 1].getIdxX());
                            MainActivity.this.enemyShips[i].setIdxY(MainActivity.this.enemyShips[i - 1].getIdxY());
                        } else {
                            MainActivity.this.enemyShips[i].setIdxX(MainActivity.this.enemyShips[nxtIdx].getIdxX() + (MainActivity.this.winWidth / 2));
                            if (i == 4) {
                                increaseScore(4);
                                MainActivity.this.enemyShips[i].setIdxY(MainActivity.this.getRandom(100, MainActivity.this.winHeight - 100));
                                MainActivity.this.initialFireIndex = MainActivity.this.enemyShips[i].getIdxY();
                                setFireGap();
                            } else {
                                MainActivity.this.enemyShips[i].setIdxY(MainActivity.this.getRandom(55, MainActivity.this.winHeight - this.warrior.getHeight()));
                            }
                        }
                    }
                    if (MainActivity.this.enemyShips[i].getIdxX()> MainActivity.this.winWidth / 3 && MainActivity.this.enemyShips[i].getIdxX() < MainActivity.this.winWidth - 30 && MainActivity.this.enemyShips[i].type == "Enemy_follow") {
                        MainActivity.this.enemyShips[i].setIdxY(MainActivity.this.warrior_Y);
                    }
                    if (MainActivity.this.enemyShips[i].getIdxX() > MainActivity.this.winWidth / 3 && (i == 4 || i == 5)) {
                        MainActivity.this.enemyShips[i] = motionOfFires(MainActivity.this.enemyShips[i]);
                    }

                    int warrior_x = enemyShips[0].getIdxX();
                    int warrior_y = warrior_Y;
                    int warrior_width = warrior.getWidth();
                    int warrior_height = warrior.getHeight();

                    int enemy_x = enemyShips[i].getIdxX();
                    int enemy_y = enemyShips[i].getIdxY();
                    int enemy_height = enemyShips[i].ship.getHeight();

                    if (enemy_x <= warrior_x + warrior_width) {
                        if (warrior_height <= enemy_height) {
                            if ((warrior_y >= enemy_y && warrior_y <= enemy_y + enemy_height)
                                    || (warrior_y + warrior_height >= enemy_y && warrior_y + warrior_height <= enemy_y + enemy_height)
                                    || (warrior_y >= enemy_y && warrior_y + warrior_height <= enemy_y + enemy_height)) {

                                if (colliding_enemy != i) {
                                    lifeCount = lifeCount - 1;
                                    v.vibrate(80);
                                }
                                flag = false;
                                colliding_enemy = i;
                            }
                        } else {
                            if ((enemy_y >= warrior_y && enemy_y <= warrior_y + warrior_height)
                                    || (enemy_y + enemy_height >= warrior_y && enemy_y + enemy_height <= warrior_y + warrior_height)
                                    || (enemy_y >= warrior_y && enemy_y + enemy_height <= warrior_y + warrior_height)) {

                                if (colliding_enemy != i) {
                                    lifeCount = lifeCount - 1;
                                    v.vibrate(80);
                                }
                                flag = false;
                                colliding_enemy = i;
                            }
                        }
                    }
                    this.canvas.drawBitmap(MainActivity.this.enemyShips[i].ship, MainActivity.this.enemyShips[i].getIdxX(), MainActivity.this.enemyShips[i].getIdxY(), new Paint());
                } else {
                    if (flag) {
                        this.canvas.drawBitmap(MainActivity.this.enemyShips[i].ship, 0, MainActivity.this.warrior_Y, new Paint());
                    } else {
                        flag = true;
                    }
                }
                i++;
                if(lifeCount == 0){
                    isGameOver = true;
                }
            }}
        }

        public Bitmap getScaledImage(Bitmap image,float ratio) {
            return Bitmap.createScaledBitmap(image, (int)(getScaledWidth(image)*ratio), (int)(getScaledHeight(image)*ratio), true);
        }

        public Bitmap getScaledImage(Bitmap image) {
            return Bitmap.createScaledBitmap(image, getScaledWidth(image), getScaledHeight(image), true);
        }
        public int getScaledHeight(Bitmap image) {
            return Math.round(((float) image.getHeight()) / getScale(image));
        }

        public int getScaledWidth(Bitmap image) {
            return Math.round(((float) image.getWidth()) / getScale(image));
        }

        public float getScale(Bitmap image) {
            return ((float) image.getHeight()) / ((float) MainActivity.this.winHeight);
        }

        public void initShips() {
            int idxX = MainActivity.this.winWidth;
            for (int i = 0; i < MainActivity.this.enemyShips.length; i++) {
                String type = getShipType(i);
                Bitmap ship = getShipBitmap(type);
                if (i == 0) {
                    MainActivity.this.enemyShips[i] = new EnemyShips(ship, 0, MainActivity.this.warrior_Y, 0, type);
                } else if (i == 5) {
                    MainActivity.this.enemyShips[i] = new EnemyShips(ship, MainActivity.this.enemyShips[i - 1].idxX, MainActivity.this.enemyShips[i - 1].idxY, 5, type);
                } else {
                    int idxY;
                    if (i == 4) {
                        idxY = MainActivity.this.getRandom(100, MainActivity.this.winHeight - 100);
                        MainActivity.this.initialFireIndex = idxY;
                        setFireGap();
                    } else {
                        idxY = MainActivity.this.getRandom(55, MainActivity.this.winHeight - this.warrior.getHeight());
                    }
                    MainActivity.this.enemyShips[i] = new EnemyShips(ship, idxX + ((i - 1) * (MainActivity.this.winWidth / 2)), idxY, 5, type);
                }
            }
        }

        public Bitmap getShipBitmap(String type) {
            if (type.equals("Warrior")) {
                return this.warrior;
            }
            if (type.equals("Enemy_follow") || type.equals("Enemy_normal")) {
                return BitmapFactory.decodeResource(getResources(), R.drawable.enemy_ship1);
            }
            return BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        }

        public String getShipType(int i) {
            if (i == 0) {
                return "Warrior";
            }
            if (i == 2) {
                return "Enemy_follow";
            }
            if (i == 4) {
                return "Enemy_fire_up";
            }
            if (i == 5) {
                return "Enemy_fire_down";
            }
            return "Enemy_normal";
        }

        public void setFireGap() {
            if (MainActivity.this.initialFireIndex <= MainActivity.this.winHeight / 2) {
                MainActivity.this.fireGap = MainActivity.this.getRandom(100, MainActivity.this.initialFireIndex);
            }
            if (MainActivity.this.initialFireIndex > MainActivity.this.winHeight / 2) {
                MainActivity.this.fireGap = MainActivity.this.getRandom(100, MainActivity.this.winHeight - MainActivity.this.initialFireIndex);
            }
        }

        public EnemyShips motionOfFires(EnemyShips enemyShips) {
            if (enemyShips.getIdxX() <= MainActivity.this.winWidth / 2) {
                if (enemyShips.type.equals("Enemy_fire_up")) {
                    if (enemyShips.getIdxY() > MainActivity.this.initialFireIndex - MainActivity.this.fireGap) {
                        enemyShips.idxY -= 3;
                        enemyShips.setIdxY(enemyShips.idxY);
                    }
                } else if (enemyShips.getIdxY() < MainActivity.this.fireGap + MainActivity.this.initialFireIndex) {
                    enemyShips.idxY += 3;
                    enemyShips.setIdxY(enemyShips.idxY);
                }
            }
            return enemyShips;
        }
    }

    public MainActivity() {
        this.bgIndex = 0;
        this.warrior_Y = 0;
        this.initialFireIndex = 0;
        this.fireGap = 0;
        this.enemyShips = new EnemyShips[7];
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mySurfaceView = new MySurfaceView(this);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        setContentView(this.mySurfaceView);
        v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        gameThreadPaused = false;
    }

    protected void onResume() {
        super.onResume();
        this.mySurfaceView.onResumeMySurfaceView();
    }

    protected void onPause() {
        super.onPause();
        if(!isGameOver) {
            gameThreadPaused = true;
        }
        this.mySurfaceView.onPauseMySurfaceView();
    }

    @Override
    public void onBackPressed(){
        if(gameThreadPaused || isGameOver) {
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
        else {
            gameThreadPaused = true;
        }
    }

    public void initWindow() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.winWidth = size.x;
        this.winHeight = size.y;
    }

    public void pauseOnTouchEvent(int x,int y){
        int width = mySurfaceView.resume.getWidth();
        int yIdx = (winHeight)-(mySurfaceView.resume.getHeight()*2);
        int offset = mySurfaceView.overlay.getWidth()/3;
        int initialX = (winWidth-mySurfaceView.overlay.getWidth())/2-mySurfaceView.resume.getWidth()/2;
        int x1 = initialX+offset/2;
        int x2 = x1+offset;
        int x3 = x2+offset;

        if(y>=yIdx){
            if(x>=x1&&x<=x1+width){
                gameThreadPaused = false;
            }
            else if(x>=x2&&x<=x2+width){
                score =0;count=0;lifeCount=5;colliding_enemy=-1;
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else if(x>=x3&&x<=x3+width){
                score =0;count=0;lifeCount=5;colliding_enemy=-1;
                Intent mainIntent = new Intent(this, Main2Activity.class);
                startActivity(mainIntent);
                finish();
            }
            else{

            }
        }
    }

    public void menuClickOnGameOver(int x,int y){
        int width = mySurfaceView.resume.getWidth();
        int offset = mySurfaceView.overlay.getWidth()/2;
        int yIdx = (winHeight-mySurfaceView.overlay.getHeight())/2+mySurfaceView.overlay.getHeight()-(int)(mySurfaceView.restart.getHeight()*1.5);
        int initialX = (winWidth-mySurfaceView.overlay.getWidth())/2-mySurfaceView.resume.getWidth()/2;
        int x1 = initialX+offset/2;
        int x2 = x1+offset;

        if(y>=yIdx){
            if(x>=x1&&x<=x1+width){
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else if(x>=x2&&x<=x2+width){
                Intent mainIntent = new Intent(this, Main2Activity.class);
                startActivity(mainIntent);
                finish();
            }
            else{

            }
        }
    }

    public void performTouchEvents(int x, int y,boolean gameThreadPaused) {
        if (gameThreadPaused) {
            pauseOnTouchEvent(x,y);
        }
        else if(isGameOver){
            menuClickOnGameOver(x,y);
        }
        else {
            if(!isGameOver) {
                if (y > (this.winHeight / 2)) {
                    if (x < 200 && this.warrior_Y < this.winHeight - 100) {
                        this.warrior_Y += 30;
                    }
                    if (x > this.winWidth - 200 && this.warrior_Y > 55) {
                        this.warrior_Y -= 30;
                    }
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case ItemTouchHelper.UP /*1*/:
                performTouchEvents((int)event.getX(), (int)event.getY(),gameThreadPaused);
                break;
        }
        return super.onTouchEvent(event);
    }

    public int getRandom(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
