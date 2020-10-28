
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_image)

        btnUserGallery = findViewById(R.id.btnUserGallery)
        btnUserCamera = findViewById(R.id.btnUserCamera)
        imgUserImage = findViewById(R.id.imgUserImage)
        imgPassProduct = findViewById(R.id.imgPassProduct)
        txtUserDisplay = findViewById(R.id.txtUserDisplay)
        imgUserDisplay = findViewById(R.id.imgUserDisplay)

        imgUserImage.visibility = View.GONE

        val gettingTryOnImage = intent
        val a = gettingTryOnImage.extras

        if (a != null) {
            val i = a["userPassImage"] as String?
            val imgUri = i.toString()
            Glide.with(imgPassProduct).load(imgUri).into(imgPassProduct)
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val photo = data?.extras?.get("data") as Bitmap
                val myCameraIntent = Intent(this@UserImageActivity, TryOnActivity::class.java)
                myCameraIntent.putExtra("CameraUserImage", photo)

                startActivity(myCameraIntent)
                imgUserImage.setImageBitmap(photo)
                imgUserImage.visibility = View.VISIBLE
                imgUserDisplay.visibility = View.INVISIBLE
                txtUserDisplay.visibility = View.INVISIBLE
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val myGalleryIntent = Intent(this@UserImageActivity, TryOnActivity::class.java)
                myGalleryIntent.putExtra("GalleryUserImage", data?.data)
                myGalleryIntent.putExtra("userPassImage", imgPassProduct.drawable.toBitmap())
                startActivity(myGalleryIntent)
                imgUserImage.setImageURI(data?.data)
                imgUserImage.visibility = View.VISIBLE
                imgUserDisplay.visibility = View.INVISIBLE
                txtUserDisplay.visibility = View.INVISIBLE
            } catch (e: Exception) {

            }
        }

        btnUserGallery.setOnClickListener {
            val storageIntent = Intent()
            storageIntent.type = "image/*"
            storageIntent.action = Intent.ACTION_GET_CONTENT
            galleryLauncher.launch(storageIntent)
        }

        btnUserCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }

    }

    private fun checkPermission(permission:String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(
                this@UserImageActivity, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){

            showMessageOKCancel(
                "You need to allow camera usage"
            ) { _, _ ->
                ActivityCompat.requestPermissions(this@UserImageActivity, arrayOf(permission), requestCode)
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_STORAGE_PERMISSION_CODE
                )
            }
            else{
                Toast.makeText(this@UserImageActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    WRITE_STORAGE_PERMISSION_CODE
                )
            }
            else{
                Toast.makeText(this@UserImageActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if(requestCode== WRITE_STORAGE_PERMISSION_CODE)
        {
            if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this@UserImageActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onBackPressed() {
        if(imgUserImage.isVisible){
            imgUserImage.visibility = View.GONE
            txtUserDisplay.visibility = View.VISIBLE
            imgUserDisplay.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

    override fun onStart() {
        checkPermission( android.Manifest.permission.CAMERA,
            CAMERA_PERMISSION_CODE)
        super.onStart()
    }

}