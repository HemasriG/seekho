package com.app.seekho.screens

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.seekho.adapters.AnimeListAdapter
import com.app.seekho.data.AnimeList
import com.app.seekho.databinding.ActivityMainBinding
import com.app.seekho.network.ApiEndPoint
import org.json.JSONObject
import java.util.HashMap

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var animeList = ArrayList<AnimeList>()
    val TAG = "Seekho"
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        runOnUiThread(Runnable {
            progressDialog = ProgressDialog(this@HomeActivity)
            progressDialog!!.setMessage("Fetching Data!..")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        })
        val path = ApiEndPoint.ANIME_LIST
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,path,null,
            com.android.volley.Response.Listener<JSONObject> { response ->
            if (response!=null){
                if (progressDialog!!.isShowing)
                    progressDialog!!.cancel()
                if (response.has("data")){
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val dataJsonObject = data.getJSONObject(i)
                        var title = ""
                        if (dataJsonObject.getString("title_english")=="null"){
                            title = dataJsonObject.getString("title")
                        }else
                            title = dataJsonObject.getString("title_english")
                        val noOfEpisodes = dataJsonObject.getInt("episodes").toString()
                        val rating = dataJsonObject.getDouble("score").toString()
                        val posterImage = dataJsonObject.getJSONObject("images").getJSONObject("jpg").getString("image_url")
                        val animeId = dataJsonObject.getInt("mal_id")
                        val animeData = AnimeList(title,noOfEpisodes,rating,posterImage,animeId)
                        animeList.add(animeData)
                    }
                    Log.d("animeList",animeList.toString())
                    refreshAdapter(animeList)
                }
            }
        }, com.android.volley.Response.ErrorListener { error ->
            if (progressDialog!!.isShowing)
                progressDialog!!.cancel()
            VolleyLog.e(TAG, "/post request fail! Error: ${error.message}")
        })
        {
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        Volley.newRequestQueue(applicationContext).add(jsonObjectRequest)
    }

    private fun refreshAdapter(animeList: ArrayList<AnimeList>) {
        binding.recyclerView.adapter = AnimeListAdapter(this,animeList)
    }
}