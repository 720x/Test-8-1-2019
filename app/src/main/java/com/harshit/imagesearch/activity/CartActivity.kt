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

    lateinit var rvcart: Recyc