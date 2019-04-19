package com.kiriakos_kotsis.androidexercise.main

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiriakos_kotsis.androidexercise.R
import com.kiriakos_kotsis.androidexercise.entities.Comment
import com.kiriakos_kotsis.androidexercise.entities.Post
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

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
                      else intent.extras.get(MasterAdapter.PostHolder.POST_KEY) as Post
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

    class CommentsAsyncTask(private val activity:DetailActivity): AsyncTask<String, Unit, Unit>() {

        override fun doInBackground(vararg p0: String?) {
            val url = URL(MasterActivity.request_url + "posts/" + activity.currentPost.id + "/comments")
            var httpClient:HttpURLConnection? = null

            if (p0[0].equals("POST")) {
                try {
                    httpClient = url.openConnection() as HttpURLConnection
                    httpClient.requestMethod = "POST"
                    httpClient.readTimeout = 10000
                    httpClient.connectTimeout = 15000
                    httpClient.requestMethod = "POST"
                    httpClient.doInput = true
                    httpClient.doOutput = true
                    httpClient.setRequestProperty("Content-Type", "application/json")
                    httpClient.connect()
                    val outputStream = httpClient.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                    writer.write(p0[1])
                    writer.flush()
                    writer.close()
                    outputStream.close()
                    if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                        val stream = BufferedInputStream(httpClient.inputStream)
                        val data: String = readStream(inputStream = stream)
                        println("data: $data")
                    } else {
                        println("ERROR ${httpClient.responseCode}")
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                } finally {
                    httpClient?.disconnect()
                }
            }
            else {
                // Retrieve all comments
                val jsonResponse = JSONArray(url.readText())

                for (i in 0 until jsonResponse.length()) {
                    val postJson: JSONObject = jsonResponse.getJSONObject(i)

                    val id: Int = postJson.getInt("id")
                    val author: String = postJson.getString("author")
                    val email: String = postJson.getString("email")
                    val comment: String = postJson.getString("comment")

                    activity.comments.add(Comment(id, activity.currentPost.id, author, email, comment))
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            activity.updateUI()
        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }
}