package com.app.seekho.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.seekho.R
import com.app.seekho.data.AnimeList
import com.app.seekho.databinding.AnimeListBinding
import com.app.seekho.screens.AnimeDetailsActivity
import com.app.seekho.screens.HomeActivity
import com.squareup.picasso.Picasso

class AnimeListAdapter (
    internal var activity: HomeActivity,
    var animeListData: ArrayList<AnimeList>,
    ) : RecyclerView.Adapter<AnimeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AnimeListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.anime_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animeData = animeListData.get(position)
        holder.title.text = animeData.title
        holder.score.text = "Rating : "+animeData.rating
        holder.noOfEp.text = "Episodes : "+animeData.noOfEpisodes
        Picasso.get().load(animeData.posterImage).into(holder.posterImg)
        holder.cardView.setOnClickListener {
            AnimeDetailsActivity.animeId = animeData.animieId
            activity.startActivity(Intent(activity,AnimeDetailsActivity::class.java))
        }
    }

    class ViewHolder (itemView : AnimeListBinding) : RecyclerView.ViewHolder(itemView.root){
        val title = itemView.title
        val score = itemView.score
        val noOfEp = itemView.noOfEp
        val posterImg = itemView.posterImg
        val cardView = itemView.cardView
    }

    override fun getItemCount(): Int {
        return animeListData.size
    }

}