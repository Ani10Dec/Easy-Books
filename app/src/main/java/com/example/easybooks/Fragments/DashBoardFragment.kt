package com.example.easybooks.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.easybooks.Adapter.BookItemAdapter
import com.example.easybooks.R
import com.example.easybooks.modal.BookItemModal
import com.example.easybooks.util.AppHelper
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashBoardFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: BookItemAdapter
    lateinit var progressLayout: RelativeLayout
    val bookList = arrayListOf<BookItemModal>()
    private val ratingComparator = Comparator<BookItemModal> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search_bar) {
        } else if (id == R.id.filer) {
            Collections.sort(bookList, ratingComparator)
            bookList.reverse()
        }
        adapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        setHasOptionsMenu(true)

        progressLayout = view.findViewById(R.id.progress_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        progressLayout.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(activity)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (AppHelper().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val dataArray = it.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                val itemObj = dataArray.getJSONObject(i)
                                val bookId = itemObj.getString("book_id")
                                val bookName = itemObj.getString("name")
                                val bookAuthor = itemObj.getString("author")
                                val bookPrice = itemObj.getString("price")
                                val bookRating = itemObj.getString("rating")
                                val bookImage = itemObj.getString("image")

                                val bookItem =
                                    BookItemModal(
                                        bookId,
                                        bookImage,
                                        bookName,
                                        bookAuthor,
                                        bookPrice,
                                        bookRating
                                    )
                                bookList.add(bookItem)
                                adapter = BookItemAdapter(activity as Context, bookList)
                                recyclerView.adapter = adapter

                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(activity as Context, e.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "b887e3a84c9b09"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
//                activity?.finish()
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
}