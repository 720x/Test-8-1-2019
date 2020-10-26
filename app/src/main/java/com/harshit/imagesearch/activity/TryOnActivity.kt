
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

        if (a != null) {
            val i = a["userPassImage"] as Bitmap?
            tryOnImage.setImageBitmap(i)
        }

        val gettingGalleryUserImage = intent
        val b = gettingGalleryUserImage.extras

        if (b != null) {
            val j = b["GalleryUserImage"] as Uri?
            val imgUri = Uri.parse(j.toString())
            Glide.with(imgUser).load(imgUri).into(imgUser)
        }

        val gettingCameraUserImage = intent
        val c = gettingCameraUserImage.extras

        if (c != null) {
            val k = c["CameraUserImage"] as Bitmap?
            imgUser.setImageBitmap(k)
        }


        tryOnImage.setOnTouchListener(OnTouchListener { _, event ->
            var xDown = 0f
            var yDown = 0f
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    xDown = event.x
                    yDown = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val movedX: Float = event.x
                    val movedY: Float = event.y
                    val distanceX = movedX - xDown
                    val distanceY = movedY - yDown
                    tryOnImage.x = tryOnImage.x + distanceX
                    tryOnImage.y = tryOnImage.y + distanceY

                    xDown - movedX
                    yDown - movedY
                }
            }
            true
        })



    }
}