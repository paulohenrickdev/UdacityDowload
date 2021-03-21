package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var animationWidth = 0f

    private var text = resources.getString(R.string.button_name)
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var valueAnimator: ValueAnimator = ValueAnimator()
    private var circleValueAnimator = ValueAnimator()
    private var sweepAngle = 0
    private var radius = 0f

    private var bgColor = 0
    private var textColor = 0
    private var animationColor = 0


    init {
        isClickable = true
        applyCustom(context, attrs)
    }

    private fun applyCustom(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            bgColor = getColor(R.styleable.LoadingButton_completedButtonColor, 0)
            animationColor = getColor(R.styleable.LoadingButton_loadingAnimationColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }


    fun setNewButtonState(state: ButtonState) {
        Log.d("loadingButton", "state setNewButtonStateCalled")
        buttonState = state
    }

    private fun setUpAnimators() {

        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        val duration = 1000L
        valueAnimator.duration = duration
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE


        valueAnimator.addUpdateListener {
            paint.color = animationColor
            animationWidth = valueAnimator.animatedValue as Float
            invalidate()
        }
        valueAnimator.start()

        circleValueAnimator = ValueAnimator.ofInt(0, 360)
        circleValueAnimator.apply {
            this.duration = 1000L
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                sweepAngle = animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
            }
            ButtonState.Loading -> {
                isEnabled = false
                text = resources.getString(R.string.button_loading)
                setUpAnimators()
                invalidate()
            }
            ButtonState.Completed -> {
                isEnabled = true; text = resources.getString(R.string.button_name); invalidate()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {

            paint.color = bgColor
            canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)

            if (buttonState == ButtonState.Loading) {
                paint.color = animationColor
                canvas.drawRect(
                        0f, 0f,
                        animationWidth,
                        heightSize.toFloat(), paint
                )
                drawCircle(canvas)
            }
            paint.color = textColor
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = resources.getDimension(R.dimen.default_text_size)
            canvas.drawText(text, widthSize * .5f, (heightSize * .5f - (paint.ascent() +paint.descent())/2), paint)
        }
    }

    private fun drawCircle(canvas: Canvas?) {
        if (canvas != null) {
            paint.apply {
                color = Color.YELLOW
                style = Paint.Style.FILL
            }
            canvas.drawArc(
                    (measuredHeight * .35f + measuredWidth * .7f), measuredHeight * .35f,
                    measuredHeight * .7f + measuredWidth * .7f,
                    measuredHeight * .7f,
                    0f, sweepAngle.toFloat(), false, paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        radius = (min(w, h) * .3f)
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        setNewButtonState(ButtonState.Clicked)
        invalidate()
        return true
    }

}