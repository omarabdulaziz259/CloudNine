package com.example.cloudnine.model.dataSource.remote.model

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    var `1h`: Double? = null,

    @SerializedName("3h")
    var `3h` : Double? = null
)