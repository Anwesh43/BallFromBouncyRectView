package com.anwesh.uiprojects.linkedballfrombouncysqview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.ballfrombouncyrectview.BallFromBouncyRectView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BallFromBouncyRectView.create(this)
    }
}
