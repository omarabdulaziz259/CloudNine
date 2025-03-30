package com.example.cloudnine.model.dataSource.remote.model

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    var all: Int? = null
)