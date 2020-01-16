package org.firstinspires.ftc.robotcontroller.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Doodler extends View {

    public Doodler(Context c){
        super(c);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setStrokeWidth(5);
        p.setColor(Color.RED);
        canvas.drawRect(20,20,20,20,p);
        super.onDraw(canvas);
    }
}
