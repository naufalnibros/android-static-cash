package co.id.naufalnibros.myapplication.data.response

import com.google.gson.annotations.SerializedName

data class Item(
    val date: String,
    var status: Boolean = false,
    @SerializedName("label") val label: String,
    @SerializedName("nb_visits") val nb_visits: Int,
)
