package com.example.cloudnine.model

import com.example.cloudnine.model.dataSource.remote.model.Clouds
import com.example.cloudnine.model.dataSource.remote.model.Coord
import com.example.cloudnine.model.dataSource.remote.model.Main
import com.example.cloudnine.model.dataSource.remote.model.Rain
import com.example.cloudnine.model.dataSource.remote.model.Sys
import com.example.cloudnine.model.dataSource.remote.model.Weather
import com.example.cloudnine.model.dataSource.remote.model.Wind
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("coord")
    var coord: Coord? = Coord(),
    @SerializedName("weather")
    var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("base")
    var base: String? = null,
    @SerializedName("main")
    var main: Main? = Main(),
    @SerializedName("visibility")
    var visibility: Int? = null,
    @SerializedName("wind")
    var wind: Wind? = Wind(),
    @SerializedName("rain")
    var rain: Rain? = Rain(),
    @SerializedName("clouds")
    var clouds: Clouds? = Clouds(),
    @SerializedName("dt")
    var dt: Long? = null,
    @SerializedName("sys")
    var sys: Sys? = Sys(),
    @SerializedName("timezone")
    var timezone: Int? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("cod")
    var cod: Int? = null
)