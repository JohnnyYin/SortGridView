package com.johnnyyin.sortgridview.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class WaterFallChildAnimationController extends ChildAnimationControllerBase<AnimationInfo> {

    public static class Builder extends ChildAnimationControllerBase.Builder<ChildAnimationControllerBase> {
        protected SortGridView sortGridView;

        public Builder sortGridView(SortGridView sortGridView) {
            this.sortGridView = sortGridView;
            return this;
        }

        @Override
        public WaterFallChildAnimationController build() {
            checkArgument();
            return new WaterFallChildAnimationController(this);
        }
    }

    protected final SortGridView mSortGridView;

    public WaterFallChildAnimationController(Builder builder) {
        super(builder);
        mSortGridView = builder.sortGridView;
    }

    @Override
    protected Animation generateChildAnimation(AnimationInfo animationInfo) {
        int firstPosition = mSortGridView.getFirstVisiblePosition();

        View posView = mSortGridView.getChildAt(animationInfo.position - firstPosition);
        if (posView == null) {
            return null;
        }
        return new TranslateAnimation(0, 0, -posView.getTop() - posView.getHeight(), 0);
    }

}