package com.johnnyyin.sortgridview.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ChildAnimationController extends ChildAnimationControllerBase {

    public static class Builder extends ChildAnimationControllerBase.Builder {
        protected SortGridView sortGridView;

        public Builder sortGridView(SortGridView sortGridView) {
            this.sortGridView = sortGridView;
            return this;
        }

        @Override
        protected void checkArgument() {
            super.checkArgument();
            if (sortGridView == null) {
                throw new IllegalArgumentException("sortGridView is null.");
            }
        }

        @Override
        public ChildAnimationController build() {
            checkArgument();
            return new ChildAnimationController(this);
        }
    }

    protected SortGridView mSortGridView;

    private ChildAnimationController(ChildAnimationControllerBase.Builder builder) {
        super(builder);
        if (builder instanceof Builder) {
            mSortGridView = ((Builder) builder).sortGridView;
        }
    }

    /**
     * override this method to custom animation
     *
     * @param oldPos
     * @param newPos
     */
    @Override
    protected Animation generateChildAnimation(int oldPos, int newPos) {
        Animation animation;
        switch (mAnimationType) {
            case ANIMATION_TYPE_TRANSLATE:
                int firstPosition = mSortGridView.getFirstVisiblePosition();

                View newPosView = mSortGridView.getChildAt(newPos - firstPosition);
                View oldPosView = mSortGridView.getChildAt(oldPos - firstPosition);

                if (newPosView == null || oldPosView == null) {
                    return null;
//                    throw new IllegalArgumentException("position is invisible.");
                }

                TranslateAnimation translateAnimation = new TranslateAnimation(newPosView.getLeft() - oldPosView.getLeft(), 0, newPosView.getTop() - oldPosView.getTop(), 0);
                animation = translateAnimation;
                animation.setDuration(mAnimationDuration);
                if (mStartOffset > 0)
                    animation.setStartOffset(mStartOffset);
                if (mInterpolator != null)
                    animation.setInterpolator(mInterpolator);
                break;
            case ANIMATION_TYPE_ALPHA:
                animation = super.generateChildAnimation(oldPos, newPos);
                break;
            default:
                throw new IllegalStateException("please assign animation type.");
        }
        return animation;
    }

}