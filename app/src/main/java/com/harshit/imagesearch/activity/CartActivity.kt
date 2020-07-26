package com.harshit.imagesearch.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harshit.imagesearch.models.CartModel
import com.harshit.imagesearch.listeners.ICartLoadListener
import com.harshit.imagesearch.R
import com.harshit.imagesearch.adapter.cartAdapter
import java.util.ArrayList

class CartActivity : AppCompatActivity(), ICartLoadListener {

    lateinit var rvcart: RecyclerView
    lateinit var cartLoadListener: ICartLoadListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rvcart = findViewById(R.id.rvCart)

        init()
        loadCartFromFirebase()

    }

    private fun init() {
        ButterKnife.bind(this)
        cartLoadListener  = this
        val layoutManager = LinearLayoutManager(this)
        rvcart.layoutManager = layoutManager
        rvcart.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    private fun loadCartFromFirebase(){
        val cartItems: MutableList<CartModel> = ArrayList<CartModel>()

        val instance = FirebaseDatabase.getInstance()
        val cart = instance.getReference("cart")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(collection: DataSnapshot) {
                if (collection.exists()) {
                    for (CartItem in collection.children) {
                        val key = CartItem.key
                        val imgUrl = CartItem.child("imgUrl").getValue(
                            String::class.java
                        )
                        val cartItem = CartModel(key,imgUrl)
                        cartItems.add(cartItem)
                    }
                    cartLoadListener.onCartLoadSuccess(cartItems)
                } else {
                    // TODO: Just directly show error toast
                    cartLoadListener.onCartLoadFailed("Error: Firebase cart list not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Just directly show error toast
                cartLoadListener.onCartLoadFailed(error.message)
            }
        }

        cart.addListenerForSingleValueEvent(listener)
    }


    override fun onCartLoadSuccess(cartModelList: List<CartModel?>?) {
        val cartAdapter = cartAdapter(this, cartModelList as List<CartModel>)
        rvcart.adapter = cartAdapter
    }

    override fun onCartLoadFailed(message: String?) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
    }

}