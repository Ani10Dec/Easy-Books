package com.example.easybooks.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query(value = "SELECT * FROM books")
    fun getAllBooks(): List<BookEntity>

    @Query(value = "SELECT * FROM books WHERE book_id = :bookID")
    fun getBookByID(bookID: String): BookEntity

}