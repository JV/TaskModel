package com.example.taskmodel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class LineView extends View {

    private Paint paint = new Paint();
    private Canvas canvas;
    private Bitmap bitmap;
    float startX;
    float startY;
    float stopX;
    float stopY;
    private Path path;

    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LineView(Context context, Paint paint, Canvas canvas, Bitmap bitmap, float startX, float startY, float stopX, float stopY, Path path) {
        super(context);
        this.paint = paint;
        this.canvas = canvas;
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.path = path;
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

}