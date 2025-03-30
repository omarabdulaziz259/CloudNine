package com.example.cloudnine.model

import com.example.cloudnine.model.dataSource.remote.model.City
import com.example.cloudnine.model.dataSource.remote.model.List
import com.google.gson.annotations.SerializedName

data class ForecastResponse(

    @SerializedName("cod") var cod: String? = null,
    @SerializedName("message") var message: Int? = null,
    @SerializedName("cnt") var cnt: Int? = null,
    @SerializedName("list") var list: ArrayList<List> = arrayListOf(),
    @SerializedName("city") var city: City? = City()

)