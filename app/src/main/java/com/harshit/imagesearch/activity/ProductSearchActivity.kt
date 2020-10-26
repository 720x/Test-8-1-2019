
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
        const val REQUEST_TARGET_IMAGE_PATH = "REQUEST_TARGET_IMAGE_PATH"
        const val GOOGLE_APPLICATION_CREDENTIALS="/Users/harshitkumar/AndroidStudioProjects/ImageSearch/app/servicekey.json"
    }

    private lateinit var viewBinding: ActivityProductSearchBinding
    private lateinit var apiClient: ProductSearchAPIClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProductSearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.progressBar2.visibility = View.VISIBLE
        btnSearch = findViewById(R.id.txtSuggestions)
        btnSearch.setOnClickListener {
            // Display progress
            (viewBinding.ivQueryImage2.drawable as? BitmapDrawable)?.bitmap?.let{
                intent.getStringExtra(REQUEST_TARGET_IMAGE_PATH)?.let { absolutePath ->

                    viewBinding.ivQueryImage2.setImageBitmap(BitmapFactory.decodeFile(absolutePath))
                    DetectWebDetections.detectWebDetections(absolutePath)

                }
            }


        }


        // Receive the query image and show it on the screen
        

        // Initialize an API client for Vision API Product Search
        //apiClient = ProductSearchAPIClient(this)



    }
    object DetectWebDetections{



        // Finds references to the specified image on the web.
        @Throws(IOException::class)
        fun detectWebDetections(filePath: String?) {

            val requests: MutableList<AnnotateImageRequest> = ArrayList()
            val imgBytes = ByteString.readFrom(FileInputStream(filePath))
            val img = Image.newBuilder().setContent(imgBytes).build()
            val feat = Feature.newBuilder().setType(Feature.Type.WEB_DETECTION).build()
            val request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build()
            requests.add(request)
            println("tier1")
            try {
                println("tier2")
                ImageAnnotatorClient.create().use { client ->
                    val response =
                        client.batchAnnotateImages(requests)
                    val responses =
                        response.responsesList
                    for (res in responses) {
                        if (res.hasError()) {
                            System.out.format("Error: %s%n", res.error.message)
                            return
                        }

                        // Search the web for usages of the image. You could use these signals later
                        // for user input moderation or linking external references.
                        // For a full list of available annotations, see http://g.co/cloud/vision/docs
                        val annotation = res.webDetection
                        println("Entity:Id:Score")
                        println("===============")
                        for (entity in annotation.webEntitiesList) {
                            println(
                                entity.description + " : " + entity.entityId + " : " + entity.score
                            )
                        }
                        for (label in annotation.bestGuessLabelsList) {
                            System.out.format("%nBest guess label: %s", label.label)
                        }
                        println("%nPages with matching images: Score%n==")
                        for (page in annotation.pagesWithMatchingImagesList) {
                            println(page.url + " : " + page.score)
                        }
                        println("%nPages with partially matching images: Score%n==")
                        for (image in annotation.partialMatchingImagesList) {
                            println(image.url + " : " + image.score)
                        }
                        println("%nPages with fully matching images: Score%n==")
                        for (image in annotation.fullMatchingImagesList) {
                            println(image.url + " : " + image.score)
                        }
                        println("%nPages with visually similar images: Score%n==")
                        for (image in annotation.visuallySimilarImagesList) {
                            println(image.url + " : " + image.score)
                        }

                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, " ${e.message}")
                println("tier3")

            }
        }

    }








    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    /**
     * Use Product Search API to search with the given query image
     */
    private fun searchByImage(queryImage: Bitmap) {
        apiClient.annotateImage(queryImage)
            .addOnSuccessListener { showSearchResult(it) }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error calling Vision API Product Search.", error)
                showErrorResponse(error.localizedMessage)
            }
    }

    /**
     * Show search result.
     */
    private fun showSearchResult(result: List<ProductSearchResult>) {
        viewBinding.progressBar2.visibility = View.GONE

        // Update the recycler view to display the search result.

    }



    /**
     * Show Error Response
     */
    private fun showErrorResponse(message: String?) {
        viewBinding.progressBar2.visibility = View.GONE
        // Show the error when calling API.
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }


}

/**
 * Adapter RecyclerView
 */
class ProductSearchAdapter :
    ListAdapter<ProductSearchResult, ProductSearchAdapter.ProductViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ProductSearchResult>() {
            override fun areItemsTheSame(
                oldItem: ProductSearchResult,
                newItem: ProductSearchResult
            ) = oldItem.imageId == newItem.imageId && oldItem.imageUri == newItem.imageUri

            override fun areContentsTheSame(
                oldItem: ProductSearchResult,
                newItem: ProductSearchResult
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    /**
     * ViewHolder to hold the data inside
     */
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind data to views
         */
        @SuppressLint("SetTextI18n")
        fun bind(product: ProductSearchResult) {
            with(itemView) {
                findViewById<TextView>(R.id.tvProductName).text = "Name: ${product.name}"
                findViewById<TextView>(R.id.tvProductScore).text = "Similarity score: ${product.score}"
                findViewById<TextView>(R.id.tvProductLabel).text = "Labels: ${product.label}"
                // Show the image using Glide
                Glide.with(itemView).load(product.imageUri).into(findViewById(R.id.ivProduct))
            }
        }
    }
}