package com.anwesh.uiprojects.ballfrombouncyrectview

/**
 * Created by anweshmishra on 27/11/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color

val nodes : Int = 5
val scGap : Float = 0.02f
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#283593")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30
val rotDeg : Float = 30f
val hFactor : Float = 2.5f
val rFactor : Float = 3.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBallFromBouncyRect(scale : Float, size : Float, h : Float, paint : Paint) {
    val sf : Float = scale.divideScale(0, 2).sinify()
    val sc : Float = scale.divideScale(1, 2)
    save()
    rotate(rotDeg * sf)
    drawRect(RectF(-size, -size / hFactor, size, size / hFactor), paint)
    restore()
    drawCircle(0f, h * 0.5f * sc, size / rFactor, paint)
}

fun Canvas.drawBFBRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawBallFromBouncyRect(scale, size, h, paint)
    restore()
}

class BallFromBouncyRectView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}