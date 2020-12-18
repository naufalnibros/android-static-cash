package co.id.naufalnibros.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.id.naufalnibros.myapplication.data.local.dao.StaticCashDao
import co.id.naufalnibros.myapplication.data.local.models.StaticCash
import co.id.naufalnibros.myapplication.utils.DATABASE_NAME

@Database(
    entities = [
        StaticCash::class
    ], version = 1, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun savedDao(): StaticCashDao

    companion object {

        @Volatile
        private var instance: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase? {
            if (instance == null) {
                synchronized(LocalDatabase::class) {
                    if (instance == null) {
                        instance = Room
                            .databaseBuilder(
                                context.applicationContext,
                                LocalDatabase::class.java, DATABASE_NAME
                            ).build()
                    }
                }
            }

            return instance
        }

    }

}