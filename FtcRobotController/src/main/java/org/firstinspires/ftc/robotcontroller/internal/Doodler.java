package org.firstinspires.ftc.robotcontroller.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Doodler extends View {


    Paint p;
    int x,y,w,h;
    int xp,yp,wp,hp;
    Rect r;
    public Doodler(Context c){
        super(c);
        p = new Paint();
        p.setStrokeWidth(5);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        x = 50;
        y = 300;
        w = 190;
        h = 50;
        xp = this.getWidth()-y-h;
        yp = x;
        wp = h;
        hp = w;
        r = new Rect(xp,yp,xp+wp,yp+hp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(r,p);
        super.onDraw(canvas);
    }
}
