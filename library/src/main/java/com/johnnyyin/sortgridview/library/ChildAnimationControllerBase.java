package com.johnnyyin.sortgridview.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import java.util.HashMap;
import java.util.Map;

public abstract class ChildAnimationControllerBase<T extends AnimationInfo> {

    public abstract static class Builder<T> {
        protected static final int DEFAULT_ANIMATION_DURATION = 1000;

        protected int animationDuration;
        protected int numColumns;
        protected int positionTag;
        protected long startOffset;
        protected Interpolator interpolator;
        protected Animation.AnimationListener animationListener;

        public Builder<T> animationDuration(int animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        public Builder<T> startOffset(long startOffset) {
            this.startOffset = startOffset;
            return this;
        }

        public Builder<T> interpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder<T> animationListener(Animation.AnimationListener animationListener) {
            this.animationListener = animationListener;
            return this;
        }

        public Builder<T> numColumns(int numColumns) {
            this.numColumns = numColumns;
            return this;
        }

        public Builder<T> positionTag(int positionTag) {
            this.positionTag = positionTag;
            return this;
        }

        protected void checkArgument() {
            if (animationDuration <= 0) {
                animationDuration = DEFAULT_ANIMATION_DURATION;
            }
            if (numColumns <= 0) {
                throw new IllegalArgumentException("numColumns can't less than or equal to zero.");
            }
        }

        public abstract <T> T build();
    }

    protected final Map<Integer, AnimationInfo> mChildAnimationMap = new HashMap<Integer, AnimationInfo>();
    protected final int mNumColumns;
    protected final int mPositionTag;
    protected final int mAnimationDuration;
    protected final long mStartOffset;
    protected final Interpolator mInterpolator;
    protected final Animation.AnimationListener mAnimationListener;

    protected ChildAnimationControllerBase(Builder builder) {
        this.mNumColumns = builder.numColumns;
        this.mPositionTag = builder.positionTag;
        this.mAnimationDuration = builder.animationDuration;
        this.mStartOffset = builder.startOffset;
        this.mInterpolator = builder.interpolator;
        this.mAnimationListener = builder.animationListener;
    }

    public void setChildAnimation(int position, T animationInfo) {
        animationInfo.position = position;
        Animation animation = animationInfo.animation;
        if (animation == null)
            animation = generateChildAnimation(animationInfo);
        if (animation == null)
            return;
        setupChildAnimation(animation, animationInfo);
        mChildAnimationMap.put(position, animationInfo);
    }

    /**
     * override this method to custom animation
     */
    protected abstract Animation generateChildAnimation(T animationInfo);

    protected void setupChildAnimation(Animation animation, T animationInfo) {
        if (animation == null)
            return;
        if (mAnimationDuration > 0)
            animation.setDuration(mAnimationDuration);
        if (animationInfo != null && animationInfo.startOffset > 0) {
            animation.setStartOffset(animationInfo.startOffset);
        } else if (mStartOffset > 0)
            animation.setStartOffset(mStartOffset);
        if (mInterpolator != null)
            animation.setInterpolator(mInterpolator);
        if (animationInfo != null)
            animationInfo.animation = animation;
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