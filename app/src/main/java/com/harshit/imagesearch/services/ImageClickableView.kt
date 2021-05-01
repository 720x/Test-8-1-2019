
package com.harshit.imagesearch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View.LAYER_TYPE_SOFTWARE
import androidx.appcompat.widget.AppCompatImageView
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.mlkit.vision.objects.DetectedObject
import com.google.protobuf.ByteString
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

/**
 * Customize ImageView which can be clickable on some Detection Result Bound.
 */
class ImageClickableView : AppCompatImageView {

    companion object {
        private const val TAG = "ImageClickableView"
        private const val CLICKABLE_RADIUS = 40f
        private const val SHADOW_RADIUS = 10f
    }



    private val dotPaint = createDotPaint()
    private var onObjectClickListener: ((cropBitmap: Bitmap) -> Unit)? = null

    // This variable is used to hold the actual size of bounding box detection result due to
    // the ratio might changed after Bitmap fill into ImageView
    private var transformedResults = listOf<TransformedDetectionResult>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Callback when user click to detection result rectangle.
     */
    fun setOnObjectClickListener(listener: ((objectImage: Bitmap) -> Unit)) {
        this.onObjectClickListener = listener
    }

    /**
     * Draw white circle at the center of each detected object on the image
     */
    fun drawDetectionResults(results: List<DetectedObject>) {