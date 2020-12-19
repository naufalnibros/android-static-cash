package co.id.naufalnibros.myapplication.data.datasource

import androidx.lifecycle.LiveData
import co.id.naufalnibros.myapplication.data.Resource
import co.id.naufalnibros.myapplication.data.local.models.StaticCash
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.Completable
import io.reactivex.Flowable

interface StaticCashDataSource {

    interface Operation {

        fun save(item: Item): Completable

        fun find(item: Item): Flowable<StaticCash>

        fun delete(item: Item): Completable

    }

    interface Store {

        fun list(): LiveData<Resource<List<Item>>>

        fun deleteAll(): Completable

    }

}