package com.example.user.pencilgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

/**
 * Created by user on 2015/12/22.
 */
public class Molecule {

    private float mX = 0 , mY = 0  , mVx = 0 , mVy = 0 , mAx = 0 , mAy = 0 , mTheta = 0 , mAGravityX = 0 , mAGravityY = 0;
    private boolean mMoving = false , mClick = false , mSetting = false , mAppear = true , mFirst = true;
    public static float mAWindX = 0 , mAWindY = 0;
    private Paint mDotPaint;
    private final float mRadiusDp = 15 ;
    private float mRadius;
    final static double mV_const = 5 * Math.sqrt(2) / 1000; // 10 * sqrt(2)
    final static double mAcceleration = 0.06 / 1000;   //0.03 * 2

    private float mLastTraceX, mLastTraceY;
    private ArrayList<Float> mCurrentTrace, mHistoryTrace;
    private final float mTraceRadiusDp = 5;
    private float mTraceRadius;
    private Paint mTracePaint;

    Terrain mTerrain;
    Window mWindow;
    DrawView mDrawView;
    private int mTeam , mNumber;

    public Molecule(float x,float y , int i , int j  , DrawView d){

        mX = x;
        mY = y;
        mTeam = i;
        mNumber = j;
        mRadius = mRadiusDp * MainActivity.mPhoneDPI;
        mTraceRadius = mTraceRadiusDp * MainActivity.mPhoneDPI;
        mDrawView = d;
        mTerrain = mDrawView.mTerrain;
        mWindow = mDrawView.mWindow;


        mDotPaint = new Paint();
        if(i==0) {
            mDotPaint.setColor(Color.RED);
        }else{
            mDotPaint.setColor(Color.BLUE);
        }

        mCurrentTrace = new ArrayList<>();
        mHistoryTrace = new ArrayList<>();
        mTracePaint = new Paint();
        if(i==0) {
            mTracePaint.setColor(Color.MAGENTA);
        }else {
            mTracePaint.setColor(Color.GREEN);
        }

    }


    public void dead() { mAppear = false; }

    public float getX() { return mX; }
    public float getY() { return mY; }
    public float getRadius() { return mRadius;}
    public float getTheta() { return mTheta; }
    public boolean isAppear() { return mAppear; }

    public void setAWindX(float a){ mAWindX = a; }
    public void setAWindY(float a){ mAWindY = a; }
    public void setAGravityX(float a){ mAGravityX = a ;}
    public void setAGravityY(float a ){ mAGravityY = a; }

    public void DrawTrace(Canvas c){
        if (!mCurrentTrace.isEmpty()) {
            for (int i = 0; i < mCurrentTrace.size(); i = i + 2)
                c.drawCircle( mWindow.getViewPointX(mCurrentTrace.get(i)), mWindow.getViewPointY(mCurrentTrace.get(i + 1)), mTraceRadius, mTracePaint);
        }
        if (!mHistoryTrace.isEmpty()) {
            for (int i = 0; i < mHistoryTrace.size(); i = i + 2)
                c.drawCircle(mWindow.getViewPointX(mHistoryTrace.get(i)), mWindow.getViewPointY(mHistoryTrace.get(i + 1)), mTraceRadius, mTracePaint);
        }
    }

    public void DrawMolecule(Canvas c){
        if(mAppear)
            c.drawCircle(mWindow.getViewPointX(mX), mWindow.getViewPointY(mY) , mRadius, mDotPaint);
    }

    public boolean ActionDown(MotionEvent motionEvent){
        if( ( (mDrawView.mRedTurn && mTeam == 0) || (!mDrawView.mRedTurn && mTeam == 1) ) && mAppear){
            float CheckX = motionEvent.getX() - mWindow.getViewPointX(mX);
            float CheckY =  mWindow.getViewPointY(mY) - motionEvent.getY();
            if (hypot(CheckX, CheckY) <= mRadius ) {
                mClick = true;
                return true;
            }
        }
        return false;
    }

    public boolean ActionUp(MotionEvent motionEvent){
        if(mClick && mAppear) {
            double dx = mWindow.getViewPointX(mX) - motionEvent.getX();
            double dy = motionEvent.getY() - mWindow.getViewPointY(mY);
            double distance = hypot(dx, dy);
            if(!mMoving && distance <=mRadius) mClick = false;
            if (!mMoving && distance > mRadius) {

                mSetting = true;

                mVx = ((float) (abs(dx) / dx * mV_const * Math.sqrt(abs(dx)  / MainActivity.mPhoneDPI) * MainActivity.mPhoneDPI));
                mVy = ((float) (abs(dy) / dy * mV_const * Math.sqrt(abs(dy)  / MainActivity.mPhoneDPI) * MainActivity.mPhoneDPI));
                CheckMoving();
                mClick = false;

                mSetting = false;
                mFirst = true;
                return true;
            }
        }
        return false;
    }

    public void move(long dt){
        if(!mSetting && mMoving) {
            float xf = mX + mVx * dt;
            float yf = mY - mVy * dt;

            mAx = (float)(-1 * mAcceleration * cos(mTheta) * MainActivity.mPhoneDPI);
            mAy = (float)(-1 * mAcceleration * sin(mTheta) * MainActivity.mPhoneDPI);
            Log.v("BallMoving" , mAx + " " + mAy + " " + mTheta);

            float ax = mAx + mAWindX + mAGravityX;
            float ay = mAy + mAWindY + mAGravityY;

            float Vxf = mVx + ax * dt;
            float Vyf = mVy + ay * dt;

            if(!mTerrain.isBallLegalPosition( xf , yf , mTeam , mNumber) ||
                    hypot( Vxf , Vyf) <= 0.01 * MainActivity.mPhoneDPI){
                stop();
            }else {
                mX = xf;
                mY = yf;
                mVx = Vxf;
                mVy = Vyf;
            }

            if(hypot(mX - mLastTraceX , mY - mLastTraceY ) >= mTraceRadius/3){
                mCurrentTrace.add(mX);
                mCurrentTrace.add(mY);
                mLastTraceX = mX;
                mLastTraceY = mY;
            }

            mWindow.setCenterX(mX);
            mWindow.setCenterY(mY);

            CheckMoving();

            if(mFirst) mFirst = false;
        }
    }

    public int isHit (float x , float y , float r){
        if(hypot( mX - x , mY - y) < mRadius + r && mAppear)return 2;

        for(int i = 0 ; i < mHistoryTrace.size() ; i = i + 2){
            if( hypot(mHistoryTrace.get(i) - x, mHistoryTrace.get(i + 1) - y) < mTraceRadius + r) return 1;
        }

        return 0;
    }

    private void CheckMoving(){
        boolean b = !(mVx == 0 & mVy == 0 & mAx == 0 & mAy == 0);
        if(b){
            if(mVx == 0){
                if(mVy > 0)mTheta =(float)PI / 2;
                if(mVy < 0)mTheta =(float)PI / 2 * 3;
                if(mVy == 0)mTheta =0;
            }
            if(mVx > 0)mTheta = (float)atan(mVy/mVx);
            if(mVx < 0)mTheta = (float)(atan(mVy/mVx) + PI);
        }
        Log.v("CheckMoving" , mVx + " " + mVy + " " + mTheta);

        if(b && !mMoving){
            mLastTraceX = mX;
            mLastTraceY = mY;
            mCurrentTrace.add(mX);
            mCurrentTrace.add(mY);
        }

        if(!b && mMoving){
            if(!mFirst){
                mCurrentTrace.add(mX);
                mCurrentTrace.add(mY);

                mHistoryTrace.clear();
                if(!mCurrentTrace.isEmpty()){
                    for(int i = 0 ; i < mCurrentTrace.size() ; i++)
                        mHistoryTrace.add(mCurrentTrace.get(i));
                }
                mCurrentTrace.clear();
                mDrawView.BallStop();
            }else {
                mDrawView.mBallMoving = false;
            }

        }

        mMoving = b;
    }

    private void stop(){
        mVx = 0;
        mVy = 0;
        mAx = 0;
        mAy = 0;
        mAWindX = 0;
        mAWindY = 0;
    }

}
