
package com.harshit.imagesearch.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import android.content.DialogInterface
import android.os.Environment
import android.view.View
import androidx.core.view.isVisible
import com.google.cloud.vision.v1.*
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import com.harshit.imagesearch.databinding.ActivityMainBinding

import android.util.Base64
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.harshit.imagesearch.*
import com.harshit.imagesearch.R
import com.harshit.imagesearch.adapter.productAdapter
import com.harshit.imagesearch.listeners.IProductLoadListener
import com.harshit.imagesearch.models.ProductModel
import java.io.*


class MainActivity : AppCompatActivity() {


    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var txtDisplay: TextView
    lateinit var imgImage: ImageView
    lateinit var imgDisplay: ImageView

    private val CAMERA_PERMISSION_CODE = 123
    private val READ_STORAGE_PERMISSION_CODE = 123
    private val WRITE_STORAGE_PERMISSION_CODE = 123

    private val TAG = "MyTag"

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewBinding: ActivityMainBinding

    lateinit var inputImage: InputImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        imgImage = findViewById(R.id.imgImage)
        imgDisplay = findViewById(R.id.imgDisplay)
        btnGallery = findViewById(R.id.btnUserGallery)
        btnCamera = findViewById(R.id.btnUserCamera)
        txtDisplay = findViewById(R.id.txtDisplay)


        imgImage.visibility = View.GONE
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val photo = data?.extras?.get("data") as Bitmap
                val myCameraIntent = Intent(this@MainActivity, ProductDisplayActivity::class.java)
                myCameraIntent.putExtra("CameraImage", photo)
                startActivity(myCameraIntent)
                imgImage.setImageBitmap(photo)
                inputImage = InputImage.fromBitmap(photo, 0)
                imgImage.visibility = View.VISIBLE
                txtDisplay.visibility = View.INVISIBLE
                imgDisplay.visibility = View.INVISIBLE
                setViewAndDetect(photo)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val myGalleryIntent = Intent(this@MainActivity, ProductDisplayActivity::class.java)
                myGalleryIntent.putExtra("GalleryImage", data?.data)
                startActivity(myGalleryIntent)
                inputImage = InputImage.fromFilePath(this@MainActivity, data?.data)
                imgImage.setImageURI(data?.data)
                imgImage.visibility = View.VISIBLE
                imgDisplay.visibility = View.INVISIBLE
                txtDisplay.visibility = View.INVISIBLE
                setViewAndDetect(inputImage.bitmapInternal)

            } catch (e: Exception) {

            }
        }


        btnGallery.setOnClickListener {
            val storageIntent = Intent()
            storageIntent.type = "image/*"
            storageIntent.action = Intent.ACTION_GET_CONTENT
            galleryLauncher.launch(storageIntent)
        }

        btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }




    }




    private fun setViewAndDetect(bitmap: Bitmap?) {
        bitmap?.let {
            viewBinding.imgImage.drawDetectionResults(emptyList())
            viewBinding.imgImage.setImageBitmap(bitmap)
            runObjectDetection(bitmap)
        }
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        //Create ML Kit's InputImage object
        val image = InputImage.fromBitmap(bitmap, 0)

        //Acquire detector object
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        //Feed given image to detector and setup callback
        objectDetector.process(image)
            .addOnSuccessListener { results ->
                //Keep only the FASHION_GOOD objects
                val filteredResults = results.filter { result ->