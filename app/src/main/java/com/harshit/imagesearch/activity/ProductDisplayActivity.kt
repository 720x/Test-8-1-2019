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
        iv_query_image = findViewById(R.id.iv_query_image)
        progressBar = findViewById(R.id.progressBar)
        txtProceedToTry = findViewById(R.id.txtProceedToTry)

        progressBar.visibility = View.VISIBLE



        init()
        loadProductFromFirebase()


        val gettingGalleryImage = intent
        val a = gettingGalleryImage.extras

        if (a != null) {
            val i = a["GalleryImage"] as Uri?
            val imgUri = Uri.parse(i.toString())
            Glide.with(iv_query_image).load(imgUri).into(iv_query_image)
        }

        val gettingCameraImage = intent
        val b = gettingCameraImage.extras

        if (b != null) {
            val j = b["CameraImage"] as Bitmap?
            iv_query_image.setImageBitmap(j)
        }

        txtProceedToTry.setOnClickListener {
            startActivity(Intent(this@ProductDisplayActivity, CartActivity::class.java))
        }


    }

    private fun init() {
        ButterKnife.bind(this)
        productLoadListener  = this
        val layoutManager = GridLayoutManager(this, 2)
        rvProductMain.layoutManager = layoutManager
        rvProductMain.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    private fun loadProductFromFirebase(){
        val coffees: MutableList<ProductModel> = ArrayList<ProductModel>()
        progressBar.visibility = View.INVISIBLE

        val instance = FirebaseDatabase.getInstance()
        val product = instance.getReference("product")

        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(collection: DataSnapshot) {
                if (collection.exists()) {
                    for (ProductItem in collection.children) {
                        val key = ProductItem.key
                        val imgUrl = ProductItem.child("imgUrl").getValue(
                            String::class.java
                        )
                        val coffee = ProductModel(key,imgUrl)
                        coffees.add(coffee)
                    }
                    productLoadListener.onProductLoadSuccess(coffees)
                } else {
                    // TODO: Just directly show error toast
                    productLoadListener.onProductLoadFailed("Error: Firebase menu list not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: 