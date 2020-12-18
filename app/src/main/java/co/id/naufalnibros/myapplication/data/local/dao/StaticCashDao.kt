package co.id.naufalnibros.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.id.naufalnibros.myapplication.data.local.models.StaticCash
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface StaticCashDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(data: StaticCash): Completable

    @Query("SELECT * FROM master_static_cash WHERE date = :date AND label = :label AND nb_visits = :nbVisits LIMIT 1")
    fun find(date: String, label: String, nbVisits: Int): Flowable<StaticCash>

    @Query("DELETE FROM master_static_cash WHERE date = :date AND label = :label AND nb_visits = :nbVisits")
    fun delete(date: String, label: String, nbVisits: Int): Completable

    @Query("DELETE FROM master_static_cash")
    fun truncate(): Completable

}