package co.id.naufalnibros.myapplication.ui.detail

import co.id.naufalnibros.myapplication.data.datasource.StaticCashDataSource
import co.id.naufalnibros.myapplication.data.local.dao.StaticCashDao
import co.id.naufalnibros.myapplication.data.local.models.StaticCash
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.Completable
import io.reactivex.Flowable

class DetailRepository(private val dao: StaticCashDao) : StaticCashDataSource.Operation {

    companion object {
        @Volatile
        private var instance: DetailRepository? = null

        fun getInstance(dao: StaticCashDao): DetailRepository =
            instance ?: synchronized(this) {
                instance ?: DetailRepository(dao)
            }
    }

    override fun find(item: Item): Flowable<StaticCash> {
        return dao.find(
            date = item.date,
            label = item.label,
            nbVisits = item.nb_visits
        )
    }

    override fun save(item: Item): Completable {
        return dao.save(
            StaticCash(
                date = item.date,
                label = item.label,
                nbVisits = item.nb_visits
            )
        )
    }

    override fun delete(item: Item): Completable {
        return dao.delete(
            date = item.date,
            label = item.label,
            nbVisits = item.nb_visits
        )
    }
}