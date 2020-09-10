
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
                    result.labels.indexOfFirst { it.text == PredefinedCategory.FASHION_GOOD } != -1
                }

                //Visualize the detection result
                runOnUiThread {
                    viewBinding.imgImage.drawDetectionResults(filteredResults)

                }

            }
            .addOnFailureListener {

                Log.e(TAG, it.message.toString())
            }

    }

    private fun startProductImageSearch(objectImage: Bitmap) {
        try {
            // Create file based Bitmap. We use PNG to preserve the image quality
            val savedFile = createImageFile(ProductSearchActivity.CROPPED_IMAGE_FILE_NAME)
            objectImage.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(savedFile))



            // Start the product search activity (using Vision Product Search API.).
            startActivity(
                Intent(
                    this,
                    ProductSearchActivity::class.java
                ).apply {
                    // As the size limit of a bundle is 1MB, we need to save the bitmap to a file
                    // and reload it in the other activity to support large query images.
                    putExtra(
                        ProductSearchActivity.REQUEST_TARGET_IMAGE_PATH,
                        savedFile.absolutePath
                    )
                })
        } catch (e: Exception) {
            // IO Exception, Out Of memory ....
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error starting the product image search activity.", e)
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }



    @Throws(IOException::class)
    private fun createImageFile(fileName: String): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            fileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }



    private fun checkPermission(permission:String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){

            showMessageOKCancel(
                "You need to allow camera usage"
            ) { _, _ ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
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
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode== READ_STORAGE_PERMISSION_CODE){
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkPermission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_STORAGE_PERMISSION_CODE
                    )
                }
                else{
                    Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
        }
        else if(requestCode== WRITE_STORAGE_PERMISSION_CODE)
            {
                if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }

            }

    }

    override fun onBackPressed() {
        if(imgImage.isVisible){
            imgImage.visibility = View.GONE
            txtDisplay.visibility = View.VISIBLE
            imgDisplay.visibility = View.VISIBLE
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