package com.example.easybooks.Fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.easybooks.Adapter.FavBookItemAdapter
import com.example.easybooks.Database.BookDatabase
import com.example.easybooks.Database.BookEntity
import com.example.easybooks.R


class FavouriteFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var adapter: FavBookItemAdapter
    var dbBookList = listOf<BookEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_fav)
        progressLayout = view.findViewById(R.id.progress_bar_layout)
        recyclerView.layoutManager = GridLayoutManager(activity as Context, 2)
        dbBookList = GetFavBookList(activity as Context).execute().get()
        if (activity != null) {
            progressLayout.visibility = View.GONE
            adapter = FavBookItemAdapter(activity as Context, dbBookList)
            recyclerView.adapter = adapter
        } else {
            Toast.makeText(activity as Context, "Error", Toast.LENGTH_SHORT).show()
        }

        if (dbBookList.isEmpty()) {
            Toast.makeText(activity as Context, "Data is Empty", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    class GetFavBookList(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book_db").build()
            return db.bookDao().getAllBooks()
        }
    }
}