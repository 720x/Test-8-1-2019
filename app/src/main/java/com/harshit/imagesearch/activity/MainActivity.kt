
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