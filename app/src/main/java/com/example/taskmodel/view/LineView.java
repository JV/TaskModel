package com.example.taskmodel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.taskmodel.R;

public class LineView extends AppCompatTextView {

    private Paint paint = new Paint();
    private Canvas canvas;
    private Bitmap bitmap;
    float startX;
    float startY;
    float stopX;
    float stopY;
    private Path path;
    private View view;

    public LineView(Context context) {
        super(context);
        View.inflate(context, R.layout.connection_holder, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.connection_holder, null);
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.connection_holder, null);
    }

    public LineView(Context context, Paint paint, Canvas canvas, Bitmap bitmap, float startX, float startY, float stopX, float stopY, Path path, View view) {
        super(context);

        this.paint = paint;
        this.canvas = canvas;
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.path = path;
        this.view = view;
        View.inflate(context, R.layout.connection_holder, null);
//        LayoutInflater.from(context).inflate(R.layout.connection_holder, (ViewGroup) view,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        Log.d("DRAW", "onDraw: ");
    }
}
