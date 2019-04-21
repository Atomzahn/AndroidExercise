package com.kiriakos_kotsis.androidexercise.main

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.kiriakos_kotsis.androidexercise.R
import com.kiriakos_kotsis.androidexercise.entities.Comment
import com.kiriakos_kotsis.androidexercise.entities.Post
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

/**
 * Manages the RecyclerView of the {@link DetailActivity} and handles all Views regarding
 * the current Post and its comments.
 * @param comments List of all comments of this post.
 * @param currentPost The {@link Post} which is shown by the current {@link DetailActivity}.
 */
class DetailAdapter(private val comments:ArrayList<Comment>, private val currentPost: Post) : RecyclerView.Adapter<DetailAdapter.DetailHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == 0) DetailHolder(inflater.inflate(R.layout.detail_first_row, parent, false), 0)
                else DetailHolder(inflater.inflate(R.layout.list_item_comment, parent, false), 1)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        if(holder.viewType == 0) {
            MasterAdapter.loadImageFromWeb(currentPost.image, holder.image)
            holder.author?.text = currentPost.author
            holder.description?.text = currentPost.content
        }
        else {
            val currentComment:Comment = comments[position]
            holder.name?.text = currentComment.name
            holder.email?.text = currentComment.email
            holder.comment?.text = currentComment.comment
        }
    }

    override fun getItemViewType(position: Int): Int {
        return  if(position == 0) 0 else 1
    }

    /**
     * Holds all Views of the current {@link DetailActivity}.
     * @param v The current {@link DetailActivity}
     * @param viewType ViewType for checking if current {@link DetailHolder} is the first row of the recyclerView
     */
    class DetailHolder(v: View, val viewType:Int) : RecyclerView.ViewHolder(v) {

        // Image and TextViews of the current Post
        lateinit var image: ImageView
        var author:TextView? = null
        var description: TextView? = null

        // EditText fields and Button for creating a comment
        private var newCommentName: EditText? = null
        private var newCommentEmail: EditText? = null
        private var newComment: EditText? = null
        private lateinit var postButton: Button

        // Views for comments
        var name: TextView? = null
        var email: TextView? = null
        var comment: TextView? = null

        init {
            if(viewType == 0) {
                // Find the views for the first row of the RecyclerView
                image = v.findViewById(R.id.detail_imageView)
                author = v.findViewById(R.id.detail_author)
                description = v.findViewById(R.id.detail_description)
                newCommentName = v.findViewById(R.id.detail_name_editText)
                newCommentEmail = v.findViewById(R.id.detail_email_editText)
                newComment = v.findViewById(R.id.detail_comment_editText)
                postButton = v.findViewById(R.id.detail_post_button)
                postButton.setOnClickListener {
                    postComment(v.context as DetailActivity)
                }
            }
            else {
                // Find the views for comments
                name = v.findViewById(R.id.comment_name_textView)
                email = v.findViewById(R.id.comment_email_textView)
                comment = v.findViewById(R.id.comment_textView)
            }
        }

        /**
         * Method for posting new comments.
         * @param context The current {@link DetailActivity}.
         */
        private fun postComment(context: DetailActivity) {
            val name:String = newCommentName?.text.toString()
            val email:String = newCommentEmail?.text.toString()
            val comment:String = newComment?.text.toString()

            if(!isValidName(name)) {
                newCommentName?.error = "Please enter a valid name.(max. 100 characters)"
            }
            else if(!isValidEmail(email)) {
                newCommentEmail?.error = "Please enter a valid e-mail address."
            }
            else if(!isValidComment(comment)) {
                newComment?.error = "Your comment should not exceed 300 characters nor be empty."
            }
            else {
                // Prepare JSONObject for sending
                val commentJSON = JSONObject()
                try {
                    commentJSON.put("email", email)
                    commentJSON.put("name", name)
                    commentJSON.put("comment", comment)
                } catch(e:Exception) {
                    e.printStackTrace()
                }
                // Try to post comment
                val task = DetailActivity.Companion.CommentsAsyncTask(context)
                task.execute("POST", commentJSON.toString())
            }
        }

        // Checks if given CharSequence is a correct e-mail address
        private fun isValidEmail(email:CharSequence) : Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        // Checks if given CharSequence is a correct name
        private fun isValidName(name:CharSequence) : Boolean {
            return !TextUtils.isEmpty(name) && name.length <= 100 && Pattern.compile(NAME_PATTERN).matcher(name).matches()
        }

        // Checks if the given CharSequence is not empty nor exceeds 300 characters
        private fun isValidComment(comment:CharSequence) : Boolean {
            return !TextUtils.isEmpty(comment) && comment.length <= 300
        }

        companion object {
            // Pattern for checking names
            const val NAME_PATTERN = "^[A-Za-z]+(\\s?[A-Za-z])*$"
        }
    }
}