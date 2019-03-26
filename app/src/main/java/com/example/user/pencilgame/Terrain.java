package com.example.user.pencilgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.math.BigDecimal;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

/**
 * Created by Kongou on 2015/12/23.
 */
public class Terrain {

    private float DefineX = 2.5f;
    private float DefineY = 1.2f;
    private final int t = 2;

    Window Window;
    DrawView DrawView;
    Molecule Molecule;

    double WindForce = new BigDecimal((Math.random() * Molecule.mAcceleration)).setScale(7, BigDecimal.ROUND_HALF_UP).doubleValue();
    double WindDirection = new BigDecimal(Math.round(Math.random() * 360)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    String WindDirectionViewer;
    private final float WallDepthDp = 40;
    private float WallDepth;
    private float[][] Wall;  //Left UP Right Down
    private Paint WallPaint;

    public Terrain(DrawView d){

        DrawView = d;


        DefineX = DefineX * MainActivity.mPhoneWidth;
        DefineY = DefineY * MainActivity.mPhoneHeight;

        WallDepth = WallDepthDp * MainActivity.mPhoneDPI;
        Wall = new float[4][];
        Wall[0] = new float[]{ -1*WallDepth , -1*WallDepth , 0                   , DefineY + WallDepth};
        Wall[1] = new float[]{ -1*WallDepth , -1*WallDepth , DefineX + WallDepth , 0                  };
        Wall[2] = new float[]{ DefineX      , -1*WallDepth , DefineX + WallDepth , DefineY + WallDepth};
        Wall[3] = new float[]{ -1*WallDepth , DefineY      , DefineX + WallDepth , DefineY + WallDepth};
        WallPaint = new Paint();
        WallPaint.setColor(Color.GRAY);

        GetWindDirection();

    }

    public float getWidth(){ return DefineX; }
    public float getHeight(){ return DefineY; }

    public void setWindow(Window w){ Window = w;}

    public void onDraw(Canvas c){

        for (float[] aWall : Wall) {
            c.drawRect(Window.getViewPointX(aWall[0]), Window.getViewPointY(aWall[1]), Window.getViewPointX(aWall[2]), Window.getViewPointY((aWall[3])) , WallPaint);
        }
    }

    public boolean isBallLegalPosition(float x , float y , int i , int j ){
        Molecule m = DrawView.mPlayer[i][j];

        if(x < m.getRadius() || x > DefineX - m.getRadius())
            return false;
        if(y < m.getRadius() || y > DefineY - m.getRadius())
            return false;

        for(int a = 0; a< DrawView.mPlayer.length ; a++){
            for(int b = 0; b < DrawView.mPlayer[a].length ; b++){
                if(a == i){
                    if(b != j && DrawView.mPlayer[a][b].isHit( x , y , m.getRadius()) == 2)
                        return false;
                }else{
                    if(DrawView.mPlayer[a][b].isHit( x , y , m.getRadius()) == 2)
                        DrawView.mPlayer[a][b].dead();
                    if(DrawView.mPlayer[a][b].isHit( x , y , m.getRadius()) == 1)
                        return false;
                }
            }
        }
        return true;
    }

    public void SetWind(){
        WindForce = new BigDecimal(Math.random() * Molecule.mAcceleration).setScale(7, BigDecimal.ROUND_HALF_UP).doubleValue();
        WindDirection += new BigDecimal((Math.random() - 0.5) * 60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (WindDirection >= 360){
            WindDirection -= 360;
        }
        if (WindDirection < 0){
            WindDirection += 360;
        }
        Molecule.mAWindX = (float) (WindForce * cos(WindDirection * Math.PI / 180));
        Molecule.mAWindY = (float) (WindForce * sin(WindDirection * Math.PI / 180));
        GetWindDirection();
    }

    public void GetWindDirection(){
        if(WindDirection == 0){
            WindDirectionViewer = "E";
        }
        if(WindDirection == 45){
            WindDirectionViewer = "NE";
        }
        if(WindDirection == 90){
            WindDirectionViewer = "N";
        }
        if(WindDirection == 135){
            WindDirectionViewer = "NW";
        }
        if(WindDirection == 180){
            WindDirectionViewer = "W";
        }
        if(WindDirection == 225){
            WindDirectionViewer = "SW";
        }
        if(WindDirection == 270){
            WindDirectionViewer = "S";
        }
        if(WindDirection == 315){
            WindDirectionViewer = "SE";
        }
        if(WindDirection > 0 && WindDirection < 45){
            WindDirectionViewer = "E" + new BigDecimal(WindDirection).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºN";
        }
        if(WindDirection > 45 && WindDirection < 90){
            WindDirectionViewer = "N" + new BigDecimal(90 - WindDirection).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºE";
        }
        if(WindDirection > 90 && WindDirection < 135){
            WindDirectionViewer = "N" + new BigDecimal(WindDirection - 90).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºW";
        }
        if(WindDirection > 135 && WindDirection < 180){
            WindDirectionViewer = "W" + new BigDecimal(180d - WindDirection).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºN";
        }
        if(WindDirection > 180 && WindDirection < 225){
            WindDirectionViewer = "W" + new BigDecimal(WindDirection - 180).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºS";
        }
        if(WindDirection > 225 && WindDirection < 270){
            WindDirectionViewer = "S" + new BigDecimal(270 - WindDirection).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºW";
        }
        if(WindDirection > 270 && WindDirection < 315){
            WindDirectionViewer = "S" + new BigDecimal(WindDirection - 270).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºE";
        }
        if(WindDirection > 315 && WindDirection < 360){
            WindDirectionViewer = "E" + new BigDecimal(360 - WindDirection).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "ºS";
        }

    }
}
