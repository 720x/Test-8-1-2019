
package com.harshit.imagesearch

import com.harshit.imagesearch.databinding.ActivityProductSearchBinding

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.protobuf.ByteString
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList

import com.google.cloud.vision.v1.*
import kotlin.Throws
import com.harshit.imagesearch.services.ProductSearchAPIClient
import java.lang.Exception


class ProductSearchActivity : AppCompatActivity() {

    lateinit var btnSearch: Button

    companion object {
        const val TAG = "ProductSearchActivity"
        const val CROPPED_IMAGE_FILE_NAME = "MLKitCroppedFile_"