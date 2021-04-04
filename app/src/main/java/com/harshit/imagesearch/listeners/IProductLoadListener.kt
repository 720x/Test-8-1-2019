package com.harshit.imagesearch.listeners

import com.harshit.imagesearch.models.ProductModel

interface IProductLoadListener {
    fun onProductLoadSuccess(productModelList: List<ProductModel?>?)