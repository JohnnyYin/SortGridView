package com.johnnyyin.sortgridview.library;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ExchangeChildAnimationController extends ChildAnimationControllerBase<ExchangeChildAnimationController.ExchangeAnimationInfo> {

    public static class Builder extends ChildAnimationControllerBase.Builder<ExchangeChildAnimationController> {

        @Override
        public ExchangeChildAnimationController build() {
            checkArgument();
            return new ExchangeChildAnimationController(this);
        }
    }

    public static class ExchangeAnimationInfo extends AnimationInfo {
        public int oldPos;

        public ExchangeAnimationInfo(int oldPos, Animation animation, long startOffset) {
            super(animation, startOffset);
            this.oldPos = oldPos;
            this.animation = animation;
        }
    }

    public ExchangeChildAnimationController(ChildAnimationControllerBase.Builder builder) {
        super(builder);
    }

    public void changePos(int pos1, int pos2) {
        changePos(pos1, pos2, 0);
    }

    public void changePos(int pos1, int pos2, long startOffset) {
        if (pos1 == pos2) {
            return;
        }
//        if (mChildAnimationMap.containsKey(pos1) || mChildAnimationMap.containsKey(pos2)) {
//            return;
//        }
        setChildAnimation(pos1, new ExchangeAnimationInfo(pos2, null, startOffset));
        setChildAnimation(pos2, new ExchangeAnimationInfo(pos1, null, startOffset));
    }

    @Override
    protected Animation generateChildAnimation(ExchangeAnimationInfo animationInfo) {
        int oldPosColumn = animationInfo.oldPos % mNumColumns;
        int newPosColumn = animationInfo.position % mNumColumns;
        int oldPosRow = animationInfo.oldPos / mNumColumns;
        int newPosRow = animationInfo.position / mNumColumns;
        return new TranslateAnimation(Animation.RELATIVE_TO_SELF, oldPosColumn - newPosColumn, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, oldPosRow - newPosRow, Animation.RELATIVE_TO_SELF, 0f);
    }

}