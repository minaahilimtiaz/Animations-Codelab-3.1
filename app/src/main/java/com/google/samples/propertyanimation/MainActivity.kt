/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {
        //It takes target object, the property and the values for animation
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        animator.duration = 1000
        //extension function for handling listener callbacks
        animator.disableViewDuringAnimation(rotateButton)
        animator.start()
    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        repeatAnimation(animator, translateButton)
        animator.start()
    }

    private fun scaler() {
        //An animator object can have multiple value property holders
        //PropertyValueHolder takes information about property and the value
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        repeatAnimation(animator, scaleButton)
        animator.start()
    }

    private fun fader() {
        //Alpha property is reponsible for transparency and opacity
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        repeatAnimation(animator, fadeButton)
        animator.start()
    }

    private fun colorizer() {
        /*No direct reference from android.util.property we use property name as string for
        matching. It is known as reflection*/
        var animator = ObjectAnimator.ofArgb(star.parent, "backgroundColor", Color.BLACK, Color.RED)
        animator.setDuration(500)
        repeatAnimation(animator, colorizeButton)
        animator.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()
        val newStar = createNewStar(container)
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        applyAndPlayAnimation(newStar, container,starW, starH)
    }

    private fun applyAndPlayAnimation(newStar: AppCompatImageView, container: ViewGroup, starW: Float,
        starH: Float) {
        newStar.translationX = Math.random().toFloat() * container.width - starW / 2
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH,
            container.height + starH)
        //for speed using interpolator
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION,
            (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()
        PlayAnimation(rotator, mover, container, newStar)
    }

    private fun PlayAnimation(rotator: ObjectAnimator, mover: ObjectAnimator,
        container: ViewGroup, newStar: AppCompatImageView) {
        //As we are using two different animation we have to use AnimatorSet
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()

    }

    private  fun repeatAnimation(animator: ObjectAnimator, buttonItem: Button) {
        animator.apply{
            repeatCount = 1
            //repeat the transition to move back to its initial state
            repeatMode = ObjectAnimator.REVERSE
            disableViewDuringAnimation(buttonItem)
        }
    }

    private fun createNewStar( container: ViewGroup) : AppCompatImageView {
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
        container.addView(newStar)
        return newStar
    }

    //creating extension function for handling listener callbacks to stop disruptive animtions
    private fun ObjectAnimator.disableViewDuringAnimation(view : View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

}
