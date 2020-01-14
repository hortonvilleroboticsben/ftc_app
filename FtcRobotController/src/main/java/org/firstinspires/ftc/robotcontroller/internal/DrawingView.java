package org.firstinspires.ftc.robotcontroller.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawingView extends View {

    public DrawingView(Context c){
        super(c);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setStrokeWidth(5);
        p.setColor(Color.RED);
        canvas.drawCircle(0,0,10, p);
        super.onDraw(canvas);
    }
}
