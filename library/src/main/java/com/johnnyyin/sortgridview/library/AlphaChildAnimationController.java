package com.johnnyyin.sortgridview.library;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AlphaChildAnimationController extends ChildAnimationControllerBase<AnimationInfo> {

    public static class Builder extends ChildAnimationControllerBase.Builder<AlphaChildAnimationController> {

        @Override
        public AlphaChildAnimationController build() {
            checkArgument();
            return new AlphaChildAnimationController(this);
        }
    }

    public AlphaChildAnimationController(ChildAnimationControllerBase.Builder builder) {
        super(builder);
    }

    @Override
    protected Animation generateChildAnimation(AnimationInfo animationInfo) {
        return new AlphaAnimation(0.0f, 1.0f);
    }

}