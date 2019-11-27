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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BFBNode(var i : Int, val state : State = State()) {

        private var next : BFBNode? = null
        private var prev : BFBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BFBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBFBRNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BFBNode {
            var curr : BFBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BallFromBouncyRect(var i : Int) {

        private val root : BFBNode = BFBNode(0)
        private var curr : BFBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BallFromBouncyRectView) {

        private val animator : Animator = Animator(view)
        private val bfbr : BallFromBouncyRect = BallFromBouncyRect(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bfbr.draw(canvas, paint)
            animator.animate {
                bfbr.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bfbr.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BallFromBouncyRectView {
            val view : BallFromBouncyRectView = BallFromBouncyRectView(activity)
            activity.setContentView(view)
            return view
        }
    }
}