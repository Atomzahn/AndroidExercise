package com.kiriakos_kotsis.androidexercise.main

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiriakos_kotsis.androidexercise.R
import com.kiriakos_kotsis.androidexercise.entities.Comment
import com.kiriakos_kotsis.androidexercise.entities.Post
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.URL

/**
 * Activity that displays the details of a Post.
 */
class DetailActivity : AppCompatActivity() {

    private var detailAdapter:DetailAdapter? = null
    private var comments:ArrayList<Comment> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView:RecyclerView
    private lateinit var currentPost:Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        currentPost = if(savedInstanceState != null) savedInstanceState.getSerializable(MasterAdapter.PostHolder.POST_KEY) as Post
                      else intent?.extras?.get(MasterAdapter.PostHolder.POST_KEY) as Post
        recyclerView = findViewById(R.id.detail_recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val task = CommentsAsyncTask(this)
        task.execute("GET")
    }

    fun updateUI() {
        detailAdapter = DetailAdapter(comments, currentPost)
        recyclerView.adapter = detailAdapter
    }

    companion object {
        /**
         * Background task for requesting and posting comments.
         * @param context The corresponding {@link DetailActivity}.
         */
        class CommentsAsyncTask internal constructor(context: DetailActivity) : AsyncTask<String, Unit, ArrayList<String>>() {

            // WeakReference for preventing memory leaks
            private val activityReference: WeakReference<DetailActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing)
                    return
            }

            override fun doInBackground(vararg p0: String): ArrayList<String> {
                val url = URL(MasterActivity.request_url + "posts/" + activityReference.get()!!.currentPost.id + "/comments")
                val result: ArrayList<String> = ArrayList()
                result.add(p0[0])

                // Posting a comment
                if (p0[0] == "POST") {
                    val client = OkHttpClient()
                    val body: RequestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), p0[1])
                    val request: Request = Request.Builder()
                        .url(url)
                        .post(body)
                        .build()
                    try {
                        val response: Response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            result.add("Comment posted successfully.")
                            println("RESPONSE: ${response.body()?.string()}")
                        } else {
                            result.add("An error occurred while posting comment.")
                            println("RESPONSE: ${response.body()?.string()}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // Retrieving all comments
                    val jsonResponse = JSONArray(url.readText())

                    for (i in 0 until jsonResponse.length()) {
                        val postJson: JSONObject = jsonResponse.getJSONObject(i)

                        val id: Int = postJson.getInt("id")
                        val author: String = postJson.getString("author")
                        val email: String = postJson.getString("email")
                        val comment: String = postJson.getString("comment")

                        activityReference.get()!!.comments.add(Comment(id, activityReference.get()!!.currentPost.id, author, email, comment))
                    }
                }
                return result
            }

            override fun onPostExecute(result: ArrayList<String>) {
                // Toast message for feedback
                if (result[0] == "POST")
                    Toast.makeText(activityReference.get(), result[1], Toast.LENGTH_LONG).show()
                else
                    // Update UI only if requesting comments
                    activityReference.get()!!.updateUI()
            }
        }
    }
}