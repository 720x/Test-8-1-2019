
package com.harshit.imagesearch.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.harshit.imagesearch.R

class UserImageActivity : AppCompatActivity() {

    private lateinit var btnUserGallery: Button
    private lateinit var btnUserCamera: Button
    lateinit var imgUserImage: ImageView
    lateinit var imgPassProduct: ImageView
    private lateinit var txtUserDisplay: TextView
    lateinit var imgUserDisplay: ImageView

    private val CAMERA_PERMISSION_CODE = 123
    private val READ_STORAGE_PERMISSION_CODE = 123
    private val WRITE_STORAGE_PERMISSION_CODE = 123

    private val TAG = "MyTag"

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
