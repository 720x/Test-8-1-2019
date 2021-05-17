package com.harshit.imagesearch.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.harshit.imagesearch.ProductSearchActivity
import com.harshit.imagesearch.ProductSearchResult
import com.harshit.imagesearch.SearchResultResponse
import org.json.JSONObject
import java.io.ByteArrayOutputStream





class ProductSearchAPIClient(context: Context){

    companion object {
        const val VISION_API_PRODUCT_MAX_RESULT = 5

        // Fill in the const below with your project info.
        const val VISION_API_URL = "https://vision.googleapis.com/v1"

        const val VISION_API_PROJECT_ID = "searchndtry"
        const val VISION_API_LOCATION_ID = "asia-east1"
        const val VISION_API_PRODUCT_SET_ID = "product_set0"
        const val VISION_API_KEY = "AIzaSyA-OWenpxVC-YX6MCwexLGLxotAkR6F9Zo"
    }

    // Instantiate the RequestQueue.
    private val requestQueue = Volley.newRequestQueue(context)

    /**
     * Convert an image to its Base64 representation
     */
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.c