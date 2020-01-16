package com.masiad.smartmeteo.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.slideAnimate(currentHeight: Int, newHeight: Int) {
    val slideAnimator = ValueAnimator
        .ofInt(currentHeight, newHeight)
        .setDuration(500)

    slideAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        this.layoutParams.height = value
        this.requestLayout()
    }

    val animatorSet = AnimatorSet()
    animatorSet.interpolator = AccelerateDecelerateInterpolator()
    animatorSet.play(slideAnimator)
    animatorSet.start()
}
