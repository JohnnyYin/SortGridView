package com.johnnyyin.sortgridview.library;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import java.util.HashMap;
import java.util.Map;

public class ChildAnimationControllerBase {

    public static class Builder {
        protected static final int DEFAULT_ANIMATION_DURATION = 1000;

        protected int animationDuration;
        protected int numColumns;
        protected int positionTag;
        protected int animationType;
        protected long startOffset;
        protected Interpolator interpolator;
        protected Animation.AnimationListener animationListener;

        public Builder animationDuration(int animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        public Builder animationType(int animationType) {
            this.animationType = animationType;
            return this;
        }

        public Builder startOffset(long startOffset) {
            this.startOffset = startOffset;
            return this;
        }

        public Builder interpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder animationListener(Animation.AnimationListener animationListener) {
            this.animationListener = animationListener;
            return this;
        }

        public Builder numColumns(int numColumns) {
            this.numColumns = numColumns;
            return this;
        }

        public Builder positionTag(int positionTag) {
            this.positionTag = positionTag;
            return this;
        }

        protected void checkArgument() {
            if (animationDuration <= 0) {
                animationDuration = DEFAULT_ANIMATION_DURATION;
            }
            if (animationType <= 0) {
                animationType = ANIMATION_TYPE_TRANSLATE;
            }
            if (numColumns <= 0) {
                throw new IllegalArgumentException("numColumns can't less than or equal to zero.");
            }
        }

        public ChildAnimationControllerBase build() {
            checkArgument();
            return new ChildAnimationControllerBase(this);
        }
    }

    protected class AnimationInfo {
        protected int newPos;
        protected Animation animation;

        public AnimationInfo(int newPos, Animation animation) {
            this.newPos = newPos;
            this.animation = animation;
        }
    }

    public static final int ANIMATION_TYPE_TRANSLATE = 1;
    public static final int ANIMATION_TYPE_ALPHA = 2;
    protected Map<Integer, AnimationInfo> mChildAnimationMap = new HashMap<Integer, AnimationInfo>();

    protected final int mNumColumns;
    protected final int mPositionTag;
    protected final int mAnimationType;
    protected final int mAnimationDuration;
    protected final long mStartOffset;
    protected final Interpolator mInterpolator;
    protected final Animation.AnimationListener mAnimationListener;

    protected ChildAnimationControllerBase(Builder builder) {
        this.mNumColumns = builder.numColumns;
        this.mPositionTag = builder.positionTag;
        this.mAnimationDuration = builder.animationDuration;
        this.mAnimationType = builder.animationType;
        this.mStartOffset = builder.startOffset;
        this.mInterpolator = builder.interpolator;
        this.mAnimationListener = builder.animationListener;
    }

    public void changePos(int oldPos, int newPos) {
        if (oldPos == newPos) {
            return;
        }
        if (mChildAnimationMap.containsKey(oldPos) || mChildAnimationMap.containsKey(newPos)) {
            return;
        }
        mChildAnimationMap.put(oldPos, new AnimationInfo(newPos, generateChildAnimation(oldPos, newPos)));
        mChildAnimationMap.put(newPos, new AnimationInfo(oldPos, generateChildAnimation(newPos, oldPos)));
    }

    /**
     * override this method to custom animation
     *
     * @param oldPos
     * @param newPos
     */
    protected Animation generateChildAnimation(int oldPos, int newPos) {
        Animation animation;
        switch (mAnimationType) {
            case ANIMATION_TYPE_TRANSLATE:
                int oldPosColumn = oldPos % mNumColumns;
                int newPosColumn = newPos % mNumColumns;
                int oldPosRow = oldPos / mNumColumns;
                int newPosRow = newPos / mNumColumns;
                int columnOffset = newPosColumn - oldPosColumn;
                int rowOffset = newPosRow - oldPosRow;

                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, columnOffset, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, rowOffset, Animation.RELATIVE_TO_SELF, 0f);
                animation = translateAnimation;
                break;
            case ANIMATION_TYPE_ALPHA:
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                animation = alphaAnimation;
                break;
            default:
                throw new IllegalStateException("please assign animation type.");
        }
        animation.setDuration(mAnimationDuration);
        if (mStartOffset > 0)
            animation.setStartOffset(mStartOffset);
        if (mInterpolator != null)
            animation.setInterpolator(mInterpolator);
        return animation;
    }

    public Animation getChildAnimation(int pos) {
        AnimationInfo animationInfo = mChildAnimationMap.get(pos);
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.animation;
    }

    public void removeChildAnimation(int pos) {
        mChildAnimationMap.remove(pos);
    }

    public int getPositionFromTag(View child) {
        int position = -1;
        if (child == null) {
            return position;
        }
        Object tag = mPositionTag > 0 ? child.getTag(mPositionTag) : child.getTag();
        position = Integer.class.isInstance(tag) ? (Integer) tag : -1;
        return position;
    }

    public boolean isEmpty() {
        return mChildAnimationMap.isEmpty();
    }

    public void clearAllAnimation() {
        mChildAnimationMap.clear();
    }

    public void onAnimationEnd() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationEnd(null);
        }
    }

    public void onAnimationStart() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStart(null);
        }
    }
}