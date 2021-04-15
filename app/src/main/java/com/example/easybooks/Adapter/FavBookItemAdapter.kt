package com.example.easybooks.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.easybooks.Database.BookEntity
import com.example.easybooks.R
import com.example.easybooks.activity.DescriptionActivity
import com.squareup.picasso.Picasso

class FavBookItemAdapter(val context: Context, private val bookList: List<BookEntity>) :
    RecyclerView.Adapter<FavBookItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = bookList[position]
        holder.bookName.text = currentItem.bookName
        holder.bookAuthor.text = currentItem.bookAuthor
        holder.bookRating.text = currentItem.bookRating
        holder.bookPrice.text = currentItem.bookPrice
        Picasso.get().load(currentItem.bookImg).error(R.drawable.book_image)
            .into(holder.bookImgView)

        holder.bookItem.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("bookID", currentItem.book_id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookName: TextView = view.findViewById(R.id.book_name)
        val bookAuthor: TextView = view.findViewById(R.id.book_author)
        val bookPrice: TextView = view.findViewById(R.id.book_price)
        val bookRating: TextView = view.findViewById(R.id.book_rating)
        val bookImgView: ImageView = view.findViewById(R.id.book_img)
        val bookItem: CardView = view.findViewById(R.id.fav_book_item)
    }
}