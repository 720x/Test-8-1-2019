package com.harshit.imagesearch


/**
 * Class to mapping with search result response
 */
data class SearchResultResponse(val responses: List<Response>?)

data class Response(
    val produ