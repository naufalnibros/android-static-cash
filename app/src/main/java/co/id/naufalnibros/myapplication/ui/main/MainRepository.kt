package co.id.naufalnibros.myapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import co.id.naufalnibros.myapplication.data.Resource
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.datasource.StaticCashDataSource
import co.id.naufalnibros.myapplication.data.local.dao.StaticCashDao
import co.id.naufalnibros.myapplication.data.local.models.StaticCash
import co.id.naufalnibros.myapplication.data.mapper.mapToList
import co.id.naufalnibros.myapplication.data.response.Item
import co.id.naufalnibros.myapplication.data.service.StaticCashService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class MainRepository(
    private val service: StaticCashService,
    private val dao: StaticCashDao
) : StaticCashDataSource {

    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(service: StaticCashService, dao: StaticCashDao): MainRepository =
            instance ?: synchronized(this) {
                instance ?: MainRepository(service, dao)
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

    override fun deleteAll() = dao.truncate()

    override fun list(): LiveData<Resource<List<Item>>> {
        val result = MediatorLiveData<Resource<List<Item>>>()

        result.postValue(Resource(status = Status.LOADING))

        val source = LiveDataReactiveStreams.fromPublisher(
            service.list()
                .subscribeOn(Schedulers.io())
                .flatMap { response -> response.mapToList() }
                .onErrorReturn {
                    it.message?.let { message ->
                        Resource(status = Status.ERROR, message = message)
                    }
                }
        )

        result.addSource(source) {
            result.removeSource(source)
            result.postValue(it)
        }

        return result
    }
}
