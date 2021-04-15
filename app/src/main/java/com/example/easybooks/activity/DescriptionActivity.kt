package com.example.easybooks.activity

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.easybooks.Database.BookDatabase
import com.example.easybooks.Database.BookEntity
import com.example.easybooks.R
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var bookImageView: ImageView
    lateinit var bookName: TextView
    lateinit var bookAuthor: TextView
    lateinit var bookPrice: TextView
    lateinit var bookRating: TextView
    lateinit var bookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var bookImgUrl: String
    var bookID: String? = "000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        bookImageView = findViewById(R.id.book_img)
        bookName = findViewById(R.id.book_name)
        bookAuthor = findViewById(R.id.book_author)
        bookPrice = findViewById(R.id.book_price)
        bookRating = findViewById(R.id.book_rating)
        bookDesc = findViewById(R.id.tv_book_desc)
        btnAddToFav = findViewById(R.id.btn_add_fav)
        progressLayout = findViewById(R.id.progress_bar_layout)
        progressLayout.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            bookID = intent.getStringExtra("bookID")
        } else {
            finish()
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show()
        }
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val params = JSONObject()
        params.put("book_id", bookID)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
                progressLayout.visibility = View.GONE
                try {
                    val success = it.getBoolean("success")
                    if (success) {
                        val dataObj = it.getJSONObject("book_data")
                        bookImgUrl = dataObj.getString("image")
                        bookName.text = dataObj.getString("name")
                        bookAuthor.text = dataObj.getString("author")
                        bookPrice.text = dataObj.getString("price")
                        bookRating.text = dataObj.getString("rating")
                        bookDesc.text = dataObj.getString("description")
                        Picasso.get().load(bookImgUrl).error(R.drawable.book_image)
                            .into(bookImageView)

                        val bookEntity = BookEntity(
                            bookID?.toInt() as Int,
                                bookName.text.toString(),
                            bookAuthor.text.toString(),
                            bookPrice.text.toString(),
                            bookRating.text.toString(),
                            bookDesc.text.toString(),
                            bookImgUrl
                        )
                        val checkFav =
                            DBAsyncTask(applicationContext, bookEntity, 1).execute().get()

                        if (checkFav) {
                            changeBtnAppearance("remove")
                        } else {
                            changeBtnAppearance("add")
                        }

                        btnAddToFav.setOnClickListener {
                            if (!DBAsyncTask(applicationContext, bookEntity, 1).execute().get()) {
                                val status =
                                    DBAsyncTask(applicationContext, bookEntity, 2).execute().get()
                                if (status) {
                                    Toast.makeText(
                                        this,
                                        "Book added to favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeBtnAppearance("remove")
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val status =
                                    DBAsyncTask(applicationContext, bookEntity, 3).execute().get()
                                if (status) {
                                    Toast.makeText(
                                        this,
                                        "Remove from favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeBtnAppearance("add")
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Some error occcurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.localizedMessage
                }
            }, Response.ErrorListener {
                println("Error is $it")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "b887e3a84c9b09"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }

    class DBAsyncTask(val context: Context, private val bookEntity: BookEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, BookDatabase::class.java, "book_db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    // Check DB if the book is favourite or not
                    val book: BookEntity? = db.bookDao().getBookByID(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    // Save the book in DB as favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
                    // Remove the favourite book in DB
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    private fun changeBtnAppearance(status: String) {
        when (status) {
            "add" -> {
                btnAddToFav.text = "Add to Favourites"
                btnAddToFav.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.purple_500
                    )
                )
            }

            "remove" -> {
                btnAddToFav.text = "Remove from Favourites"
                btnAddToFav.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.dark_blue
                    )
                )
            }
        }
    }
}