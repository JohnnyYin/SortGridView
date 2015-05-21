package com.johnnyyin.sortgridview.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AbsoluteExchangeChildAnimationController extends ExchangeChildAnimationController {

    public static class Builder extends ChildAnimationControllerBase.Builder<AbsoluteExchangeChildAnimationController> {
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
        public AbsoluteExchangeChildAnimationController build() {
            checkArgument();
            return new AbsoluteExchangeChildAnimationController(this);
        }
    }

    protected final SortGridView mSortGridView;

    public AbsoluteExchangeChildAnimationController(AbsoluteExchangeChildAnimationController.Builder builder) {
        super(builder);
        mSortGridView = builder.sortGridView;
    }

    @Override
    protected Animation generateChildAnimation(ExchangeAnimationInfo animationInfo) {
        int firstPosition = mSortGridView.getFirstVisiblePosition();

        View newPosView = mSortGridView.getChildAt(animationInfo.position - firstPosition);
        View oldPosView = mSortGridView.getChildAt(animationInfo.oldPos - firstPosition);

        if (newPosView == null || oldPosView == null) {
            return null;
//            throw new IllegalArgumentException("position is invisible.");
        }
        return new TranslateAnimation(oldPosView.getLeft() - newPosView.getLeft(), 0f, oldPosView.getTop() - newPosView.getTop(), 0f);
    }

}