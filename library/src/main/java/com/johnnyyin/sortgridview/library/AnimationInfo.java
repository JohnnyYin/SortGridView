package com.johnnyyin.sortgridview.library;

import android.view.animation.Animation;

public class AnimationInfo {
    protected int position;
    protected Animation animation;
    protected long startOffset;

    public AnimationInfo() {
        this(null);
    }

    public AnimationInfo(long startOffset) {
        this(null, startOffset);
    }

    public AnimationInfo(Animation animation) {
        this(animation, 0);
    }

    public AnimationInfo(Animation animation, long startOffset) {
        this.animation = animation;
        this.startOffset = startOffset;
    }
}