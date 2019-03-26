package com.example.user.pencilgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.math.BigDecimal;

import static java.lang.Math.pow;

/**
 * Created by user on 2015/12/23.
 */
public class Window {

    private float mTerrainX , mTerrainY;
    private float mCenterX , mCenterY;
    private float mWindowWidth , mWindowHeight;

    private RectF mViewPointButton , mInitButton , mToolBar ;
    private Paint mViewPointButtonPaint , mInitPaint, mToolBarPaint, mWindViewer;
    private int mRedViewPoint = 0;
    private int mBlueViewPoint = 0;

    private DrawView mDrawView;
    private Terrain mTerrain;

    private float mTouchX = 0 , mTouchY = 0;
    private float mOriginX = 0 , mOriginY = 0;
    private boolean mMoving = false;

    public Window(float TerrainX , float TerrainY , DrawView d){

        mTerrainX = TerrainX;
        mTerrainY = TerrainY;
        mCenterX = mTerrainX / 2;
        mCenterY = mTerrainY / 2;
        mDrawView = d;
        mTerrain = d.mTerrain;
        mWindowWidth = MainActivity.mPhoneWidth;
        mWindowHeight = MainActivity.mPhoneHeight * 9 / 10;

        mViewPointButton = new RectF(0, MainActivity.mPhoneHeight * 9 / 10, MainActivity.mPhoneWidth / 5, MainActivity.mPhoneHeight);
        mViewPointButtonPaint = new Paint();

        mInitButton = new RectF(MainActivity.mPhoneWidth * 4 / 5 , MainActivity.mPhoneHeight * 9 / 10 , MainActivity.mPhoneWidth , MainActivity.mPhoneHeight);
        mInitPaint = new Paint();
        mInitPaint.setColor(Color.BLACK);

        mToolBar = new RectF(0 , MainActivity.mPhoneHeight * 9 / 10 , MainActivity.mPhoneWidth , MainActivity.mPhoneHeight);
        mToolBarPaint = new Paint();
        mToolBarPaint.setColor(Color.YELLOW);

        mWindViewer = new Paint();
        mWindViewer.setColor(Color.BLACK);
        mWindViewer.setTextAlign(Paint.Align.CENTER);
        mWindViewer.setTextSize(20 * MainActivity.mPhoneDPI);
    }

    public float getViewPointX(float x){ return x - mCenterX + mWindowWidth / 2; }
    public float getViewPointY(float y){ return y - mCenterY + mWindowHeight / 2; }
    public float getTrueX(float x){ return x + mCenterX - mWindowWidth / 2;}
    public float getTrueY(float y){ return y + mCenterY - mWindowHeight / 2;}

    public void setCenterX(float x) { mCenterX = x; }
    public void setCenterY(float y) { mCenterY = y; }

    public void onDraw(Canvas c){
        if(mDrawView.mRedTurn){
            mViewPointButtonPaint.setColor(Color.RED);
        }else {
            mViewPointButtonPaint.setColor(Color.BLUE);
        }

        c.drawRect(mToolBar , mToolBarPaint);
        c.drawRect(mInitButton , mInitPaint);
        c.drawRect(mViewPointButton , mViewPointButtonPaint);
        c.drawText("WindForce: " + new BigDecimal(mTerrain.WindForce * (int) pow(10, 5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), MainActivity.mPhoneWidth / 2, MainActivity.mPhoneHeight * 0.94f, mWindViewer);
        c.drawText("WindDirection: " + mTerrain.WindDirectionViewer, MainActivity.mPhoneWidth / 2, MainActivity.mPhoneHeight * 0.97f, mWindViewer);

    }

    public boolean isTouched(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        if(mViewPointButton.contains(x , y)){
           ChangeViewPoint();
            return true;
        }

        if(mInitButton.contains(x , y)){
            mDrawView.init();
            return true;
        }

        if(mToolBar.contains( x, y )) return true;

        return false;
    }

    public void ChangeViewPoint(){
        Molecule[][] mArray = mDrawView.mPlayer;
        if (mDrawView.mRedTurn) {
            mRedViewPoint = (mRedViewPoint + 1) % mArray[0].length;
            while (!mArray[0][mRedViewPoint % mArray[0].length].isAppear() && mDrawView.GameState() == -1){
                mRedViewPoint = (mRedViewPoint + 1) % mArray[0].length;
            }
            mCenterX = mArray[0][mRedViewPoint].getX();
            mCenterY = mArray[0][mRedViewPoint].getY();
        }else {
            mBlueViewPoint = (mBlueViewPoint + 1) % mArray[1].length;
            while (!mArray[1][mBlueViewPoint % mArray[1].length].isAppear() && mDrawView.GameState() == -1){
                mBlueViewPoint = (mBlueViewPoint + 1) % mArray[1].length;
            }
            mCenterX = mArray[1][mBlueViewPoint].getX();
            mCenterY = mArray[1][mBlueViewPoint].getY();
        }
    }

    public void OriginXY(){
        mCenterX = mTerrainX / 2;
        mCenterY = mTerrainY / 2;
    }

    public boolean ActionDown(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        if(x < MainActivity.mPhoneHeight * 9 / 10){
            mTouchX = x;
            mTouchY = y;
            mOriginX = mCenterX;
            mOriginY = mCenterY;
            mMoving = true;
            return true;
        }
        return false;
    }

    public boolean ActionMoving(MotionEvent e){
        if(mMoving){
            float x = e.getX();
            float y = e.getY();
            float xf =  mOriginX - (x - mTouchX);
            float yf = mOriginY - (y - mTouchY);
            if(xf < -1 * mWindowWidth /2){
                mCenterX = -1 * mWindowWidth / 2;
            }else  if (xf > mTerrainX + mWindowWidth / 2){
                mCenterX = mTerrainX + mWindowWidth / 2;
            }else {
                mCenterX = xf;
            }

            if(yf < -1 * mWindowHeight /2){
                mCenterY = -1 * mWindowHeight / 2;
            }else  if (yf > mTerrainY + mWindowHeight / 2){
                mCenterY = mTerrainY + mWindowHeight / 2;
            }else {
                mCenterY = yf;
            }

            return true;
        }
        return false;
    }

    public boolean ActionUp(MotionEvent e){
        if(ActionMoving(e)){
            mMoving = false;
            return true;
        }
        return false;
    }
}
