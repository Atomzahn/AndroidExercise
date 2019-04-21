package com.kiriakos_kotsis.androidexercise.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.kiriakos_kotsis.androidexercise.R
import com.kiriakos_kotsis.androidexercise.entities.Post
import org.json.JSONArray
import org.json.JSONObject
import android.os.AsyncTask
import androidx.recyclerview.widget.LinearLayoutManager
import java.lang.ref.WeakReference
import java.net.URL

/**
 * The main activity of this project. Retrieves all posts from "http://excercise.born-to-create.de/posts".
 */
class MasterActivity : AppCompatActivity() {

    private var masterAdapter:MasterAdapter? = null
    private var posts:ArrayList<Post> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master)

        recyclerView = findViewById(R.id.master_recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val task = PostsAsyncTask(this)
        task.execute()
    }

    /**
     * Initializes the adapter after retrieving all posts and sets it to the RecyclerView.
     */
    fun updateUI() {
        masterAdapter = MasterAdapter(posts)
        recyclerView.adapter = masterAdapter
    }

    companion object {
        const val request_url: String = "http://excercise.born-to-create.de/"

        /**
         * Background task for receiving all posts.
         */
        private class PostsAsyncTask internal constructor(context: MasterActivity) : AsyncTask<Unit, Unit, Unit>() {

            // WeakReference for preventing memory leaks
            private val activityReference:WeakReference<MasterActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing)
                    return
            }

            override fun doInBackground(vararg p0: Unit?) {
                // Retrieve all posts
                val url: String = request_url + "posts"
                val jsonResponse = JSONArray(URL(url).readText())

                for (i in 0 until jsonResponse.length()) {
                    val postJson: JSONObject = jsonResponse.getJSONObject(i)

                    val id: Int = postJson.getInt("id")
                    val author: String = postJson.getString("author")
                    val title: String = postJson.getString("title")
                    val thumbnail: String = postJson.getString("thumbnail")
                    val image: String = postJson.getString("image")
                    val content: String = postJson.getString("content")

                    activityReference.get()!!.posts.add(Post(id, author, title, thumbnail, image, content))
                }
            }

            override fun onPostExecute(result: Unit?) {
                activityReference.get()!!.updateUI()
            }
        }
    }
}
