package com.johnnyyin.sortgridview.library;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridView;

/**
 * Created by Johnny on 15/5/15.
 */
public class SortGridView extends GridView {
    public static final String TAG = "SGV";
    private static final boolean DEBUG = true;

    private Transformation mChildTransformation;
    private ChildAnimationController mChildAnimationController;
    private int mNumColumns;
    private boolean mChildAnimating;

    public SortGridView(Context context) {
        super(context);
    }

    public SortGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SortGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
        int position = mChildAnimationController != null ? mChildAnimationController.getPositionFromTag(child) : -1;
        Animation childAnimation;
        int restoreTo = -1;
        if (position >= 0 && (childAnimation = mChildAnimationController.getChildAnimation(position)) != null) {
            restoreTo = canvas.save();
            drawChildAnimation(canvas, child, drawingTime, childAnimation, position);
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreTo);
        log("drawChild:" + child.getTag());
        return result;
    }

    private void drawChildAnimation(Canvas canvas, View child, long drawingTime, Animation a, int position) {
        final boolean initialized = a.isInitialized();
        int width = child.getWidth();
        int height = child.getHeight();
        if (!initialized) {
            a.initialize(width, height, getWidth(), getHeight());
            if (!mChildAnimating) {
                mChildAnimating = true;
                onAnimationStart();
            }
        }

        final Transformation t = getChildTransformation();
        boolean more = a.getTransformation(drawingTime, t, 1f);
        if (a.willChangeTransformationMatrix()) {
            canvas.concat(t.getMatrix());
        }

        // alpha begin
        if (child.getLayerType() == LAYER_TYPE_NONE) {
            float alpha = 1;
            float transformAlpha = t.getAlpha();
            if (transformAlpha < 1) {
                alpha *= transformAlpha;
            }
            if (alpha < 1) {
                int layerFlags = Canvas.HAS_ALPHA_LAYER_SAVE_FLAG;
                if (getClipChildren()) {
                    layerFlags |= Canvas.CLIP_TO_LAYER_SAVE_FLAG;
                }
                int scrollX = child.getScrollX();
                int scrollY = child.getScrollY();
                final int multipliedAlpha = (int) (255 * alpha);
                canvas.saveLayerAlpha(scrollX + child.getLeft(), scrollY + child.getTop(), scrollX + child.getRight(), scrollY + child.getBottom(), multipliedAlpha, layerFlags);
            }
        }
        // alpha end

        if (more) {
            invalidate();
        } else {
            mChildAnimationController.removeChildAnimation(position);
            if (mChildAnimationController.isEmpty()) {
                mChildAnimating = false;
                onAnimationEnd();
            }
        }
    }

    private Transformation getChildTransformation() {
        if (mChildTransformation == null) {
            mChildTransformation = new Transformation();
        }
        return mChildTransformation;
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumns = numColumns;
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        if (mChildAnimationController != null) {
            mChildAnimationController.clearAllAnimation();
        }
        if (mChildTransformation != null) {
            mChildTransformation.clear();
        }
        mChildAnimating = false;
    }

    public void setChildAnimationController(ChildAnimationController childAnimationController) {
        this.mChildAnimationController = childAnimationController;
    }

    public boolean changeChildPos(int oldPos, int newPos) {
        if (!canChangeChildPos()) {
            throw new IllegalStateException("can't change child position.");
        }
        int firstPosition = getFirstVisiblePosition();
        int lastPosition = getLastVisiblePosition();
        if (newPos < firstPosition || newPos > lastPosition || oldPos < firstPosition || oldPos > lastPosition) {
            return false;
        }
        if (mChildAnimationController == null) {
            mChildAnimationController = new ChildAnimationController.Builder().numColumns(mNumColumns).build();
        }
        mChildAnimationController.changePos(oldPos, newPos);
        return true;
    }

    public boolean canChangeChildPos() {
        return !mChildAnimating;
    }

    private void log(String log) {
        if (TextUtils.isEmpty(log))
            return;
        if (DEBUG) {
            Log.i(TAG, log);
        }
    }

}
