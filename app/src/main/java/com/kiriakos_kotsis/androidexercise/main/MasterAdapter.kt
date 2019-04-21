package com.kiriakos_kotsis.androidexercise.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kiriakos_kotsis.androidexercise.R
import com.kiriakos_kotsis.androidexercise.entities.Post
import com.squareup.picasso.Picasso

/**
 * Stores and manages all posts. Adapter for the recyclerView of {@link MasterActivity}.
 * @param posts List of all posts.
 */
class MasterAdapter(private val posts:ArrayList<Post>) : RecyclerView.Adapter<MasterAdapter.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_master, parent, false))
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val currentPost:Post? = posts[position]
        holder.post = currentPost
        loadImageFromWeb(currentPost?.thumbnail, holder.thumbnailImageView)
        holder.titleView.text = currentPost?.title
        holder.excerptView.text = currentPost?.content?.subSequence(0, 100)
    }

    companion object {
        /**
         * Loads an image into a given imageView.
         * @param url URL of the image file.
         * @param v The imageView the image should be loaded into.
         */
        fun loadImageFromWeb(url:String?, v:ImageView) {
            Picasso.get()
                .load(url)
                .into(v)
        }
    }

    /**
     * Holder class for managing the views of a post preview.
     */
    class PostHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var post:Post? = null
        var thumbnailImageView: ImageView = v.findViewById(R.id.list_item_image)
        var titleView: TextView = v.findViewById(R.id.list_item_title)
        var excerptView: TextView = v.findViewById(R.id.list_item_excerpt)

        init {
            v.setOnClickListener(this)
        }

        /**
         * Starts the DetailActivity of the clicked Post.
         */
        override fun onClick(v: View?) {
            val context = itemView.context
            val detailIntent = Intent(context, DetailActivity::class.java)
            detailIntent.putExtra(POST_KEY, post)
            context.startActivity(detailIntent)
        }

        companion object {
            const val POST_KEY = "POST"
        }
    }

}
