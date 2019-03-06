package com.centennial.donateblood.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.centennial.donateblood.R
import java.util.*

/**
 * Copyright (c) 2016 Arlind Hajredinaj
 *
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

class CircleCheckBox : View {
    var innerCircleRadius = 30f
    var outerCircleRadius = innerCircleRadius / 2f
    var textSize = 35f
    var borderThickness = 5f
    var tickThickness = 2f
    var textLeftPadding = 2f

    private val increment = 20f
    private val total_time = 200f
    private val mPaintPageFill = Paint(ANTI_ALIAS_FLAG)
    private val mPaintPageStroke = Paint(ANTI_ALIAS_FLAG)
    private val mPaintTick = Paint(ANTI_ALIAS_FLAG)
    private val mPaintOuter = Paint(ANTI_ALIAS_FLAG)
    private val mPaintText = Paint(ANTI_ALIAS_FLAG)
    private var firstRun = true
    private var textWrap = false
    private var timer_running = false
    private val tick_third_ = innerCircleRadius / 3
    private var draw_tick_part_one = false
    var text: String? = ""
        set(text) {
            if (text != null) {
                field = text
            }
        }

    var tickColor = Color.argb(255, 255, 255, 255)
    var textColor = Color.argb(255, 0, 0, 0)
    var outerCircleColor = Color.argb(100, 0, 207, 173)
    var innerCircleColor = Color.argb(255, 0, 207, 173)
    var circleBorderColor = Color.argb(255, 0, 207, 173)

    private var listener: OnCheckedChangeListener? = null

    var isShowOuterCircle = true

    internal var centerX = 0f
    internal var centerY = 0f
    private var isChecked = false

    private var draw_tick = false

    internal var timer = Timer()

    private var current_radius = 0.0f
    internal var time = 0f

    internal var tick_x = 0f
    internal var tick_y = 0f
    internal var tick_x_two = 0f
    internal var tick_y_two = 0f

    internal var handler = Handler()

    internal var position: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleCheckbox,
                0, 0
            )

            try {
                setTickColorHex(a.getString(R.styleable.CircleCheckbox_tickColor))
                setTextColorHex(a.getString(R.styleable.CircleCheckbox_textColor))
                isShowOuterCircle = a.getBoolean(R.styleable.CircleCheckbox_showOuterCircle, true)
                setInnerCircleColorHex(a.getString(R.styleable.CircleCheckbox_innerCircleColor))
                setOuterCircleColorHex(a.getString(R.styleable.CircleCheckbox_outerCircleColor))
                setCircleBorderColorHex(a.getString(R.styleable.CircleCheckbox_circleBorderColor))
                tickThickness = a.getDimension(R.styleable.CircleCheckbox_tickThickness, tickThickness)
                borderThickness = a.getDimension(R.styleable.CircleCheckbox_borderThickness, borderThickness)
                textLeftPadding = a.getDimension(R.styleable.CircleCheckbox_textLeftPadding, textLeftPadding)
                textSize = a.getDimension(R.styleable.CircleCheckbox_textSize, textSize)
                innerCircleRadius = a.getDimension(R.styleable.CircleCheckbox_innerCircleRadius, innerCircleRadius)
                outerCircleRadius = a.getDimension(R.styleable.CircleCheckbox_outerCircleRadius, outerCircleRadius)
                textWrap = a.getBoolean(R.styleable.CircleCheckbox_textWrap, textWrap)
                text = a.getString(R.styleable.CircleCheckbox_text)
            } finally {
                a.recycle()
            }
        }

        mPaintOuter.color = outerCircleColor
        mPaintPageFill.color = innerCircleColor
        mPaintTick.color = tickColor
        mPaintTick.strokeWidth = tickThickness * 2

        mPaintPageStroke.color = circleBorderColor
        mPaintPageStroke.strokeWidth = borderThickness
        mPaintPageStroke.style = Paint.Style.STROKE

        mPaintText.textSize = textSize
        mPaintText.color = textColor


        setOnClickListener { setChecked(!isChecked) }
    }
    // Interpolator interpolator = new BounceInterpolator();

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        centerX = innerCircleRadius + outerCircleRadius + paddingLeft.toFloat()

        centerY = (height / 2).toFloat()

        //float interpolation = Math.abs(interpolator.getInterpolation(time));

        canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaintPageStroke)

        if (isChecked) {
            if (draw_tick) {
                val tick_offset = tickThickness * 2
                if (isShowOuterCircle) {
                    canvas.drawCircle(centerX, centerY, current_radius + outerCircleRadius, mPaintOuter)
                }
                canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaintPageFill)
                if (draw_tick_part_one) {

                    canvas.drawCircle(centerX - tick_offset - tick_third_, centerY, tickThickness, mPaintTick)
                    canvas.drawLine(
                        centerX - tick_offset - tick_third_,
                        centerY,
                        tick_x - tick_offset,
                        tick_y,
                        mPaintTick
                    )
                    canvas.drawCircle(tick_x - tick_offset, tick_y, tickThickness, mPaintTick)
                } else {
                    canvas.drawCircle(centerX - tick_offset - tick_third_, centerY, tickThickness, mPaintTick)
                    canvas.drawLine(
                        centerX - tick_offset - tick_third_,
                        centerY,
                        tick_x - tick_offset,
                        tick_y,
                        mPaintTick
                    )
                    canvas.drawCircle(tick_x - tick_offset, tick_y, tickThickness, mPaintTick)

                    canvas.drawLine(centerX - tick_offset, tick_y, tick_x_two - tick_offset, tick_y_two, mPaintTick)
                    canvas.drawCircle(tick_x_two - tick_offset, tick_y_two, tickThickness, mPaintTick)
                }

            } else {
                if (isShowOuterCircle && current_radius >= innerCircleRadius - outerCircleRadius) {
                    canvas.drawCircle(centerX, centerY, current_radius + outerCircleRadius, mPaintOuter)
                }
                canvas.drawCircle(centerX, centerY, current_radius, mPaintPageFill)
            }
        } else {
            if (!firstRun) {
                canvas.drawCircle(centerX, centerY, current_radius, mPaintPageFill)
            }
        }

        if (isChecked) {
            if (!timer_running) {
                tick_x = centerX// tick_third_;
                tick_y = centerY + tick_third_
                val tick_offset = tickThickness * 2
                canvas.drawCircle(centerX - tick_offset - tick_third_, centerY, tickThickness, mPaintTick)
                canvas.drawLine(centerX - tick_offset - tick_third_, centerY, tick_x - tick_offset, tick_y, mPaintTick)
                canvas.drawCircle(tick_x - tick_offset, tick_y, tickThickness, mPaintTick)

                tick_x_two = tick_x + tick_third_ * 1.7f
                tick_y_two = tick_y - tick_third_ * 1.7f
                canvas.drawLine(centerX - tick_offset, tick_y, tick_x_two - tick_offset, tick_y_two, mPaintTick)
                canvas.drawCircle(tick_x_two - tick_offset, tick_y_two, tickThickness, mPaintTick)
                tick_x = 0f
                tick_y = 0f
                tick_x_two = 0f
                tick_x_two = 0f
            }
        }

        canvas.drawText(
            this.text!!,
            centerX + textLeftPadding + innerCircleRadius + outerCircleRadius,
            centerY + textSize / 2,
            mPaintText
        )
        firstRun = false
    }

    private fun startAnimationTimer() {
        this.post { runAnimation() }
    }

    private fun runAnimation() {
        handler.postDelayed({
            timer_running = true
            time += increment
            if (time < total_time) {

                val inc = innerCircleRadius / (total_time / increment)
                if (isChecked) {
                    current_radius = current_radius + inc
                } else {
                    current_radius = current_radius - inc
                }
                postInvalidate()
                runAnimation()
            } else {
                if (isChecked) {
                    time = 0f
                    startTickAnimation()
                } else {
                    timer_running = false
                }
            }
        }, increment.toLong())
    }

    private fun startTickAnimation() {
        handler.postDelayed({
            draw_tick_part_one = true
            timer_running = true
            draw_tick = true

            if (time == 0f) {
                tick_x = centerX - tick_third_
                tick_y = centerY
            }
            val inc_tick = tick_third_ / (total_time / increment)

            tick_x += inc_tick
            tick_y += inc_tick

            time += increment
            if (time <= total_time) {
                postInvalidate()
                startTickAnimation()
            } else {
                draw_tick_part_one = false
                time = 0f
                startTickPartTwoAnimation()
            }
        }, increment.toInt().toLong())
    }

    private fun startTickPartTwoAnimation() {
        handler.postDelayed({
            timer_running = true
            draw_tick = true
            if (time == 0f) {
                tick_x_two = tick_x
                tick_y_two = tick_y
            }

            val inc_tick = tick_third_ * 1.7f / (total_time / increment)

            tick_x_two += inc_tick
            tick_y_two -= inc_tick

            time += increment
            if (time <= total_time) {
                postInvalidate()
                startTickPartTwoAnimation()
            } else {
                timer_running = false
                draw_tick = false
                time = 0f
            }
        }, increment.toLong())
    }

    fun setTickColorHex(tick_color: String?) {
        if (tick_color != null)
            this.tickColor = Color.parseColor(tick_color)
    }

    fun setTextColorHex(color: String?) {
        if (color != null)
            this.textColor = Color.parseColor(color)
    }

    fun setInnerCircleColorHex(innerCircleColor: String?) {
        if (innerCircleColor != null)
            this.innerCircleColor = Color.parseColor(innerCircleColor)
    }

    fun setOuterCircleColorHex(outerCircleColor: String?) {
        if (outerCircleColor != null)
            this.outerCircleColor = Color.parseColor(outerCircleColor)
    }

    fun setCircleBorderColorHex(color: String?) {
        if (color != null)
            this.circleBorderColor = Color.parseColor(color)
    }

    fun setChecked(isChecked: Boolean) {
        if (!timer_running) {
            this.isChecked = isChecked
            if (listener != null)
                listener!!.onCheckedChanged(this, isChecked)
            if (isChecked) {
                tick_x = 0f
                tick_y = 0f
                tick_x_two = 0f
                tick_y_two = 0f
                current_radius = 0f
            }
            time = 0f
            startAnimationTimer()
        }
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.listener = listener
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: CircleCheckBox, isChecked: Boolean)
    }
}
