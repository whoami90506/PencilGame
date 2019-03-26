package com.example.user.pencilgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.lang.Math.*;

/**
 * Created by user on 2015/11/24.
 */
public class DrawView extends View {

    final int mFrame = 80;
    long mBallUpdateTime;

    Molecule[][] mPlayer;
    boolean mRedTurn;
    Paint mBlackPaint;

    int mLastMoleculeI;
    int mLastMoleculeJ;

    boolean mBallMoving;

    Terrain mTerrain;
    Window mWindow;

    public DrawView(Context context) {

        super(context);

        mPlayer = new Molecule[2][5];

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mBlackPaint.setTextAlign(Paint.Align.CENTER);
        mBlackPaint.setTextSize(50 * MainActivity.mPhoneDPI);

        init();
    }

    public void init(){
        mBallMoving = false;

        mRedTurn = true;

        mLastMoleculeI = 0;
        mLastMoleculeJ = 0;

        mTerrain = new Terrain(this);
        mWindow = new Window( mTerrain.getWidth() , mTerrain.getHeight() , this);
        mTerrain.setWindow(mWindow);

        for(int i = 0; i<mPlayer.length ; i++){
            for(int j = 0 ; j < mPlayer[i].length ; j++) {
                mPlayer[i][j] = new Molecule(mTerrain.getWidth() * (j + 1) / (mPlayer[i].length + 1), mTerrain.getHeight() / 6+ mTerrain.getHeight()* 2*i/3, i, j , this);
                Log.v("Ball Position" , i +" "+ j + " " + mTerrain.getWidth() * (j + 1) / (mPlayer[i].length + 1) + " " + mTerrain.getHeight() / 6+ mTerrain.getHeight()* 2*i/3);
            }
        }


        mBallUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTerrain.onDraw(canvas);

        if(GameState() == -1) PassTime(System.currentTimeMillis());

        for (Molecule[] aMPlayer : mPlayer) {
            for (Molecule anAMPlayer : aMPlayer) {
                anAMPlayer.DrawTrace(canvas);
            }
        }

        for (Molecule[] aMPlayer : mPlayer) {
            for (Molecule anAMPlayer : aMPlayer) {
                anAMPlayer.DrawMolecule(canvas);
            }
        }

        mPlayer[mLastMoleculeI][mLastMoleculeJ].DrawTrace(canvas);
        mPlayer[mLastMoleculeI][mLastMoleculeJ].DrawMolecule(canvas);

        mWindow.onDraw(canvas);

        if(GameState() == 0)canvas.drawText("Red Wins!", MainActivity.mPhoneWidth / 2, MainActivity.mPhoneHeight / 2, mBlackPaint);
        if(GameState() == 1)canvas.drawText("Blue Wins!", MainActivity.mPhoneWidth/2 , MainActivity.mPhoneHeight/2 , mBlackPaint);

        invalidate();
    }

    public void BallStop(){
        mRedTurn = !mRedTurn;
        mBallMoving = false;
        mTerrain.SetWind();
        mWindow.OriginXY();

    }

    public int GameState(){
        for (Molecule[] aMPlayer : mPlayer) {
            for (Molecule anAMPlayer : aMPlayer) {
                if (anAMPlayer == null) return -1;
            }
        }
        boolean defeat = true;
        for (int i = 0; i < mPlayer[0].length ; i++){
            if(mPlayer[0][i].isAppear()){
                defeat = false;
                break;
            }
        }
        if(defeat)return 1;

        defeat = true;
        for (int i = 0; i <mPlayer[1].length ; i++){
            if(mPlayer[1][i].isAppear()){
                defeat = false;
                break;
            }
        }
        if(defeat)return 0;
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.v("MotionAction" , "ActionDown");
                if(GameState() == -1) {
                    if (!mBallMoving) {
                        if(mWindow.isTouched(motionEvent)){
                            break;
                        }

                        boolean flag = false;
                        for(Molecule[] subBall : mPlayer){
                            for(Molecule Ball : subBall){
                                if(Ball.ActionDown(motionEvent)){
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        Log.v("ActionDown BallTouch" , String.valueOf(flag));
                        if(flag) break;

                        if(mWindow.ActionDown(motionEvent)) break;
                    }
                }else {
                    init();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v("MotionAction" , "ActionUp");
                if(!mBallMoving) {
                    boolean flag = false;
                    for(int i = 0 ; i< mPlayer.length ; i++) {
                        for(int j = 0; j < mPlayer[i].length ; j++) {
                            if (mPlayer[i][j].ActionUp(motionEvent)){
                                mLastMoleculeI = i;
                                mLastMoleculeJ = j;
                                flag = true;
                                mBallMoving = true;
                                break;
                            }
                        }
                    }
                    if(flag)break;

                    if(mWindow.ActionUp(motionEvent))break;
                }
                break;
            //After finishing ACTION_UP, it will run ACTION_DOWN once.

            case MotionEvent.ACTION_MOVE:
                //og.v("MotionAction" , "ActionMove");
                if(mWindow.ActionMoving(motionEvent))break;
                break;

        }
       return true;
    }

    private void PassTime(long t){
        if( t - mBallUpdateTime >= 1000/mFrame){
            long dt = t- mBallUpdateTime;

            for (Molecule[] aMPlayer : mPlayer) {
                for (Molecule anAMPlayer : aMPlayer) {
                    anAMPlayer.move(dt);
                }
            }

            mBallUpdateTime = t;
        }
    }

}
