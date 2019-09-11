package com.example.taskmodel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
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
    private boolean matchFirst;
    private boolean belongs;
    private boolean matchLast;
    private boolean matchUnique;
    private boolean noMatch;

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

    public LineView(Context context, Canvas canvas, Bitmap bitmap, float startX, float startY, float stopX, float stopY, Path path, View view, Boolean matchFirst, Boolean matchLast, Boolean matchUnique, Boolean belongs, Boolean noMatch) {
        super(context);
        this.canvas = canvas;
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.path = path;
        this.view = view;
        this.matchFirst = matchFirst;
        this.matchLast = matchLast;
        this.belongs = belongs;
        this.matchUnique = matchUnique;
        this.noMatch = noMatch;
        View.inflate(context, R.layout.connection_holder, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
