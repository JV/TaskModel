package com.example.taskmodel.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class LineTextView extends AppCompatTextView {

    private Paint paint = new Paint();

    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawLine(0, 0, 0, getHeight(), paint);
    }
}