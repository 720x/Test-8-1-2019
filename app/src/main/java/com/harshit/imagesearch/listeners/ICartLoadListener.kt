
package com.harshit.imagesearch.listeners

import com.harshit.imagesearch.models.CartModel

interface ICartLoadListener {

    fun onCartLoadSuccess(cartModelList: List<CartModel?>?)

    fun onCartLoadFailed(message: String?)

}