package com.harshit.imagesearch.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.harshit.imagesearch.listeners.IProductLoadListener
import com.harshit.imagesearch.R
import com.harshit.imagesearch.adapter.productAdapter
import com.harshit.imagesearch.models.ProductModel
import java.util.ArrayList

class ProductDisplayActivity : AppCompatActivity(), IProductLoadListener {

    lateinit var rvProductMain: RecyclerView
    lateinit var productLoadListener: IProductLoadListener
    lateinit var iv_query_image: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var txtProceedToTry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_display)
        rvProductMain = findViewById(R.id.rvProductMain)
