package com.holytrinity.api.sample

data class BooksResponse(
    val status: String,
    val books: List<BookResponse>
)

data class BookResponse(
    val id: String?,
    val book_title: String,
    val cover_image: String,
    val content: String,
    val file_path: String,
    val message: String? = null
)