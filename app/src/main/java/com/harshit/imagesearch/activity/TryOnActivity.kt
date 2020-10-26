
package com.harshit.imagesearch.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.harshit.imagesearch.R

class TryOnActivity : AppCompatActivity() {

    private lateinit var tryOnImage: ImageView
    private lateinit var imgUser: ImageView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_on)

        tryOnImage = findViewById(R.id.tryOnImage)
        imgUser = findViewById(R.id.imgUser)


        val gettingTryOnImage = intent
        val a = gettingTryOnImage.extras
