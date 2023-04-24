package com.wishbook.catalog.stories;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.wishbook.catalog.R;

public class CircleProgressBarDrawable extends ProgressBarDrawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mLevel = 0;
    private int maxLevel = 10000;

    StoryStatusView storyStatusView;
    View view;

    public CircleProgressBarDrawable(StoryStatusView storyStatusView, Context context) {
        this.storyStatusView = storyStatusView;
        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.circle_progress_fresco, null);
        view.measure(View.MeasureSpec.getSize(view.getMeasuredWidth()), View.MeasureSpec.getSize(view.getMeasuredHeight()));
        Log.e("TAG", "CircleProgressBarDrawable: Width" + view.getMeasuredWidth() + "\n Height==>"+view.getMeasuredHeight());
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (getHideWhenZero() && mLevel == 0) {
            if(storyStatusView!=null){
                storyStatusView.resume();
            }
            return;
        }


        drawBar(canvas, maxLevel, getBackgroundColor());
        drawBar(canvas, mLevel, getColor());
    }

    private void drawBar(Canvas canvas, int level, int color) {

        Rect bounds = getBounds();
        RectF rectF = new RectF((float) (bounds.right * .4), (float) (bounds.bottom * .4),
                (float) (bounds.right * .6), (float) (bounds.bottom * .6));
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        int x = canvas.getWidth();
        int y = canvas.getHeight();
        if(level == 10000){
            if(storyStatusView!=null) {
                storyStatusView.resume();
            }
        } else {
            if(storyStatusView!=null) {
                storyStatusView.pause();
            }
        }
        if (level != 0) {
            view.layout(0,100,canvas.getWidth(),canvas.getHeight());
            view.draw(canvas);

            //canvas.drawCircle(x/2,y/2,100,mPaint);
            // canvas.drawArc(rectF,0,360,true,mPaint);
           // canvas.drawArc(rectF, 0, (float) (level * 360 / maxLevel), false, mPaint);
            //canvas.drawArc(rectF, 0, (float) (level * 360 / maxLevel), false, mPaint);
        }
    }
}
