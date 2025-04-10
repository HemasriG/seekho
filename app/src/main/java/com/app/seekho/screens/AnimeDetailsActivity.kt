package com.app.seekho.screens

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.seekho.R
import com.app.seekho.data.AnimeDetails
import com.app.seekho.data.Genre
import com.app.seekho.databinding.ActivityAnimeDetailsBinding
import com.app.seekho.network.ApiEndPoint
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.HashMap

class AnimeDetailsActivity : AppCompatActivity() {

    private lateinit var animeDetailsBinding: ActivityAnimeDetailsBinding
    var genreList = ArrayList<Genre>()
    val TAG = "Seekho"
    var videoId = ""
    var posterImage = ""
    private lateinit var animeDetails : AnimeDetails
    var readMoreText : Boolean = false
    private var progressDialog: ProgressDialog? = null

    companion object {
        var animeId = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animeDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_anime_details)
        setContentView(animeDetailsBinding.root)
        animeDetailsBinding.readMore.setOnClickListener {
            if (readMoreText){
                animeDetailsBinding.synopsis.text = ""
                animeDetailsBinding.synopsis.text = animeDetails.shortSynopsis
                animeDetailsBinding.readMore.text = "Read More"
                readMoreText = false
            }else{
                animeDetailsBinding.synopsis.text = ""
                animeDetailsBinding.synopsis.text = animeDetails.synopsis
                animeDetailsBinding.readMore.text = "Read Less"
                readMoreText = true
            }
        }
        animeDetailsBinding.close.setOnClickListener {
            animeDetailsBinding.youtubePlayerView.release()
            finish()
        }
        loadData()
    }

    private fun loadData() {
        runOnUiThread(Runnable {
            progressDialog = ProgressDialog(this@AnimeDetailsActivity)
            progressDialog!!.setMessage("Fetching Data!..")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        })
        val path = ApiEndPoint.ANIME_DETAILS+ animeId
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,path,null,
            com.android.volley.Response.Listener<JSONObject> { response ->
            if (response!=null){
                if (progressDialog!!.isShowing)
                    progressDialog!!.cancel()
                if (response.has("data")){
                    val dataResponse = response.getJSONObject("data")
                    var title = ""
                    if (dataResponse.getString("title_english")=="null"){
                        title = dataResponse.getString("title")
                    }else
                        title = dataResponse.getString("title_english")
                    val noOfEpisodes = dataResponse.getInt("episodes").toString()
                    val rating = dataResponse.getDouble("score").toString()
                    val synopsis = dataResponse.getString("synopsis")
                    val shortSynopsis : String = synopsis.substring(0,200)
                    val genre = dataResponse.getJSONArray("genres")
                    videoId = dataResponse.getJSONObject("trailer").getString("youtube_id")
                    posterImage = dataResponse.getJSONObject("images").getJSONObject("jpg").getString("image_url")
                    for (i in 0 until genre.length()) {
                        val jsonObject = genre.getJSONObject(i)
                        val id = jsonObject.getString("mal_id")
                        val genreType = jsonObject.getString("name")
                        genreList.add(Genre(id,genreType))
                    }
                    animeDetails = AnimeDetails(title,synopsis,shortSynopsis,videoId,genreList,rating,noOfEpisodes,posterImage)
                    Log.d("animeList",animeDetails.toString())
                    Log.d("videoId",videoId)
                    animeDetailsBinding.animeDetail=animeDetails
                    updateUI(genreList)
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

    private fun updateUI(genres: ArrayList<Genre>) {
        for (i in 0 until genres.size){
            if (i == genres.size-1)
                animeDetailsBinding.genre.append(genres.get(i).type)
            else
                animeDetailsBinding.genre.append(genres.get(i).type+",")
        }
        if (videoId=="null"){
            animeDetailsBinding.posterImage.visibility= View.VISIBLE
            animeDetailsBinding.youtubePlayerView.visibility=View.GONE
            Picasso.get().load(posterImage).into(animeDetailsBinding.posterImage)
        }else{
            animeDetailsBinding.youtubePlayerView.visibility=View.VISIBLE
            lifecycle.addObserver(animeDetailsBinding.youtubePlayerView) //important for lifecycle management
            animeDetailsBinding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f) // Load and play the video
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animeDetailsBinding.youtubePlayerView.release()
    }

}