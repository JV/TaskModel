package com.example.taskmodel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class LineTextView extends AppCompatTextView {

    private Paint paint = new Paint();
    private Canvas canvas;
    private Bitmap bitmap;
    float startX;
    float startY;
    float stopX;
    float stopY;
    private Path path;

    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LineTextView(Context context, Paint paint, Canvas canvas, Bitmap bitmap, float startX, float startY, float stopX, float stopY, Path path) {
        super(context);
        this.paint = paint;
        this.canvas = canvas;
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.path = path;
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
