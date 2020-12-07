package com.tokyonth.weather.widget.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

class SearchAnim {

    private static final long DURATION = 200;
    private AnimListener mListener;

    public interface AnimListener {

        void onHideAnimationEnd();

        void onShowAnimationEnd();
    }

    private void actionOtherVisible(final boolean isShow, final View animView) {
        Animator anim;
        if (isShow) {
            anim = ViewAnimationUtils.createCircularReveal(animView, animView.getWidth() / 2,
                    animView.getHeight() / 2, 0, animView.getWidth());
        } else {
            anim = ViewAnimationUtils.createCircularReveal(animView, animView.getWidth() / 2,
                    animView.getHeight() / 2, animView.getWidth(), 0);
        }

        animView.setVisibility(View.VISIBLE);
        anim.setDuration(DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isShow) {
                    animView.setVisibility(View.VISIBLE);
                    if (mListener != null) mListener.onShowAnimationEnd();
                } else {
                    animView.setVisibility(View.GONE);
                    if (mListener != null) mListener.onHideAnimationEnd();
                }
            }
        });
        anim.start();
    }

    void show(View showView) {
        actionOtherVisible(true, showView);
    }

    void hide(View hideView) {
        actionOtherVisible(false, hideView);
    }

    void setAnimListener(AnimListener listener) {
        mListener = listener;
    }

}