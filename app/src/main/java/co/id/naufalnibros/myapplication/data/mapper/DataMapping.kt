package co.id.naufalnibros.myapplication.data.mapper

import co.id.naufalnibros.myapplication.data.Resource
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.Flowable

fun Map<String, List<Item>>.mapToList(): Flowable<Resource<List<Item>>> {
    val list: ArrayList<Item> = ArrayList()

    for (entry in this.entries) {
        val key: String = entry.key
        val value: List<Item> = entry.value

        for (data in value) {
            list.add(
                Item(
                    date = key,
                    label = data.label,
                    nb_visits = data.nb_visits
                )
            )
        }
    }

    return Flowable.just(list.mapToResource(Status.SUCCESS))
}

fun ArrayList<Item>.mapToResource(status: Status): Resource<List<Item>> {
    return when (status) {
        Status.LOADING -> Resource(status = Status.LOADING)
        Status.SUCCESS -> Resource(status = Status.SUCCESS, data = this)
        Status.ERROR -> Resource(status = Status.ERROR, message = "Terjadi Kesalahan")
    }
}