package co.id.naufalnibros.myapplication.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import co.id.naufalnibros.myapplication.utils.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class StaticCash(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") var date: String? = null,
    @ColumnInfo(name = "label") var label: String? = null,
    @ColumnInfo(name = "nb_visits") var nbVisits: Int = 0,
)
