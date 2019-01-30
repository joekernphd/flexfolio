package com.example.cryptodaddies.flexfolio.splash;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by Raymond on 1/24/2018.
 */

public final class SpinningImage {
    private static int DURATION = 500;
    private static int OFFSET = DURATION/2 ;
    private static int REPEAT_COUNT = 0;

    private SpinningImage() {
        throw new AssertionError("Dont instantiate this.");
    }

    public static void build(ImageView image, Animation.AnimationListener listener) {
        RotateAnimation animation = getBasicSpinningAnimation(DURATION, OFFSET, REPEAT_COUNT);
        animation.setAnimationListener(listener);
        image.startAnimation(animation);
    }

    private static RotateAnimation getBasicSpinningAnimation(int duration, int offset, int repeatCount) {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(duration);
        rotateAnimation.setStartOffset(offset);
        rotateAnimation.setRepeatCount(repeatCount);

        return rotateAnimation;
    }
}
