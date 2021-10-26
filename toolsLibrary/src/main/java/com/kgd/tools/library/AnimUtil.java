package com.kgd.tools.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * 动画工具类
 * 属性动画：
 *  1、透明度：alpha
 *  2、旋转度数：rotation、rotationX、rotationY
 *  3、平移：translationX、translationY
 *  4、缩放：scaleX、scaleY
 */
public class AnimUtil {

    /**
     * 属性动画
     * 同一位置的两个view上下翻转  切换隐藏显示
     * @param oldView  隐藏的view
     * @param newView  要显示的view
     * @param time  动画时长 ms
     */
    public static void rotationView(final View oldView, final View newView, final long time) {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(oldView, "rotationX", 0, 90);
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(newView, "rotationX", -90, 0);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));

        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                animator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(time).start();
    }

    /**
     *
     *
     * @param oldView
     * @param newView
     * @param time
     */

    /**
     * 属性动画
     * 同一位置的两个view 缩放到放大，隐藏到显示
     * @param oldView   隐藏的view
     * @param newView   要显示的view
     * @param propertyName 属性名称 scaleX 或 scaleY
     * @param time  动画时长 ms
     */
    public static void scaleView(final View oldView, final View newView, String propertyName, final long time){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(oldView, propertyName, 1f, 0.2f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(newView, propertyName, 0.2f, 1f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setDuration(time);
//        animatorSet.playTogether(objectAnimator, objectAnimator2);
//        animatorSet.start();

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                objectAnimator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(time).start();
    }

    /**
     * 属性动画
     * 同一位置的两个view上下平移
     * @param oldView  隐藏的view
     * @param newView  要显示的view
     * @param time  动画时长 ms
     */
    public static void translateYView(final View oldView, final View newView, final long time){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(oldView, "translationY", 0, -oldView.getHeight());
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(newView, "translationY", newView.getHeight(), 0);
        objectAnimator2.setInterpolator(new OvershootInterpolator(2.0f));

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                objectAnimator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(time).start();
//        objectAnimator2.setDuration(1000).start();
    }

    /**
     * 属性动画
     * 同一位置的两个view左右平移
     * @param oldView  隐藏的view
     * @param newView  要显示的view
     * @param time  动画时长 ms
     */
    public static void translateXView(final View oldView, final View newView, final long time){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(oldView, "translationX", 0, -oldView.getWidth());
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(newView, "translationX", -newView.getWidth(), 0);
        objectAnimator2.setInterpolator(new OvershootInterpolator(2.0f));

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                objectAnimator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(time).start();
//        objectAnimator2.setDuration(1000).start();
    }

    /**
     * 属性动画
     * 同一位置的两个view虚化到显示
     * @param oldView  隐藏的view
     * @param newView  要显示的view
     * @param time  动画时长 ms
     */
    public static void alphaView(final View oldView, final View newView, final long time){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(oldView, "alpha", 1, 0);
        //第三个参数是可变长参数，这个就跟ValueAnimator中的可变长参数的意义一样了，就是指这个属性值是从哪变到哪。像我们上面的代码中指定的就是将oldview的alpha属性从1变到0
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(newView, "alpha", 0, 1);
        objectAnimator2.setInterpolator(new OvershootInterpolator(2.0f));

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                objectAnimator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(time).start();
//        objectAnimator2.setDuration(1000).start();
    }
}
