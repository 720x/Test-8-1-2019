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
    val boundingPoly: BoundingPoly,
    val results: List<Result>
)

data class BoundingPoly(
    val normalizedVertices: List<NormalizedVertex>
)

data class NormalizedVertex(
    val x: Double,
    val y: Double
)

data class Result(
    val product: Product,
    val score: Double,
    val image: String
)

data class Product(
    val name: String,
    val displayName: String,
    val productCategory: String,
    val productLabels: List<ProductLabel>
)

data class ProductLabel(
    val key: String,
    val value: String
)


/**
 * Transf