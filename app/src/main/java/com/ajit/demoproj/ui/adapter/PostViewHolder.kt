package com.ahmedabdelmeged.pagingwithrxjava.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ajit.demoproj.R
import com.ajit.demoproj.data.local.Post
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_user.view.*


class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindTo(postData: Post?) {
        itemView.title.text = postData?.title
        itemView.subTitle.text = postData?.description

        Glide.with(itemView.context)
            .load(postData?.imageHref)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
            ).into(itemView.image)

        itemView.image.visibility = if (true) View.VISIBLE else View.GONE

        itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(itemView.context, "${postData?.title}", Toast.LENGTH_SHORT).show()

        })
    }

    companion object {
        fun create(parent: ViewGroup): PostViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_user, parent, false)
            return PostViewHolder(view)
        }
    }

}