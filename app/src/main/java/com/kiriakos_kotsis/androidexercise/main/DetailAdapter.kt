package com.kiriakos_kotsis.androidexercise.main

import android.content.Context
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


class DetailAdapter(private val comments:ArrayList<Comment>, private val currentPost: Post) : RecyclerView.Adapter<DetailAdapter.DetailHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        var inflater:LayoutInflater = LayoutInflater.from(parent.context)
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

    class DetailHolder(v: View, val viewType:Int) : RecyclerView.ViewHolder(v) {
        // Views for the first row of the recyclerview
        lateinit var image: ImageView
        lateinit var postButton: Button
        var author:TextView? = null
        var description: TextView? = null
        var newCommentName: EditText? = null
        var newCommentEmail: EditText? = null
        var newComment: EditText? = null

        // Views for comments
        var name: TextView? = null
        var email: TextView? = null
        var comment: TextView? = null

        init {
            if(viewType == 0) {
                image = v.findViewById(R.id.detail_imageView)
                author = v.findViewById(R.id.detail_author)
                description = v.findViewById(R.id.detail_description)
                newCommentName = v.findViewById(R.id.detail_name_editText)
                newCommentEmail = v.findViewById(R.id.detail_email_editText)
                newComment = v.findViewById(R.id.detail_comment_editText)
                postButton = v.findViewById(R.id.detail_post_button)
                postButton.setOnClickListener {
                    postComment(v.context)
                }
            }
            else {
                name = v.findViewById(R.id.comment_name_textView)
                email = v.findViewById(R.id.comment_email_textView)
                comment = v.findViewById(R.id.comment_textView)
            }
        }

        private fun postComment(context: Context) {
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
                val commentJSON = JSONObject()
                try {
                    commentJSON.put("email", email)
                    commentJSON.put("name", name)
                    commentJSON.put("comment", comment)
                } catch(e:Exception) {
                    e.printStackTrace()
                }
                val task = DetailActivity.CommentsAsyncTask(context as DetailActivity)
                task.execute("POST", commentJSON.toString())
            }
        }

        private fun isValidEmail(email:CharSequence) : Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        private fun isValidName(name:CharSequence) : Boolean {
            return !TextUtils.isEmpty(name) && name.length <= 100 && Pattern.compile(NAME_PATTERN).matcher(name).matches()
        }

        private fun isValidComment(comment:CharSequence) : Boolean {
            return !TextUtils.isEmpty(comment) && comment.length <= 300
        }

        companion object {
            const val NAME_PATTERN = "^[a-z]+(\\s?[a-z])*$"
        }
    }

}