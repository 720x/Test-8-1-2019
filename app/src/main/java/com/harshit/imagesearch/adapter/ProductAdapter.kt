package com.harshit.imagesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.harshit.imagesearch.R
import com.harshit.imagesearch.models.CartModel
import com.harshit.imagesearch.models.ProductModel
import java.lang.Exception


class productAdapter(private val context: Context, productModelList: List<ProductModel>) :
    RecyclerView.Adapter<productAdapter.MyProductViewHolder>() {
    private var productModelList: List<ProductModel> = productModelList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_list_item_design, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {

        val currentItem = productModelList[position]
        Glide.with(holder.imgProduct).load(currentItem.imgUrl).into(holder.imgProduct)

        holder.btnAddToCart.setOnClickListener(View.OnClickListener {
            addToCart(productModelList[position])
        })





    }

    private fun addToCart(productModel: ProductModel) {
        val userFavs = FirebaseDatabase
            .getInstance()
            .getRe