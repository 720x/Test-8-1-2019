package com.harshit.imagesearch


/**
 * Class to mapping with search result response
 */
data class SearchResultResponse(val responses: List<Response>?)

data class Response(
    val productSearchResults: ProductSearchResults?
)

data class ProductSearchResults(
    val indexTime: String,
    val results: List<Result>,
    val productGroupedResults: List<ProductGroupedResult>
)

data class ProductGroupedResult(
    val boundingP