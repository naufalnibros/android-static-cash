package co.id.naufalnibros.myapplication.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val date: String,
    var status: Boolean = false,
    @SerializedName("label") val label: String,
    @SerializedName("nb_visits") val nb_visits: Int,
) : Parcelable
