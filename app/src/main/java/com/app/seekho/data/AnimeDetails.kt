package com.app.seekho.data

data class AnimeDetails(
    var title:String,
    var synopsis : String,
    var shortSynopsis : String,
    var videoUrl:String,
    var genres : ArrayList<Genre>,
    var rating:String,
    var episodes:String,
    var imageUrl:String
)

data class Genre (
    var id:String,var type:String)
