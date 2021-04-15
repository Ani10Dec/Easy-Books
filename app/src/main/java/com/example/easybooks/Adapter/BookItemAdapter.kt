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
import com.example.easybooks.R
import com.example.easybooks.activity.DescriptionActivity
import com.example.easybooks.modal.BookItemModal
import com.squareup.picasso.Picasso

class BookItemAdapter(val context: Context, private val itemList: ArrayList<BookItemModal>) :
    RecyclerView.Adapter<BookItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bookName.text = item.bookName
        holder.bookAuthor.text = item.bookAuthor
        holder.bookRating.text = item.bookRating
        holder.bookPrice.text = item.bookPrice
        Picasso.get().load(item.bookImage).error(R.drawable.book_image).into(holder.bookImg)

        holder.bookItem.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("bookID", item.bookID)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookItem: CardView = view.findViewById(R.id.list_item);
        val bookName: TextView = view.findViewById(R.id.book_name)
        val bookImg: ImageView = view.findViewById(R.id.book_img)
        val bookAuthor: TextView = view.findViewById(R.id.book_author)
        val bookRating: TextView = view.findViewById(R.id.book_rating)
        val bookPrice: TextView = view.findViewById(R.id.book_price)
    }
}