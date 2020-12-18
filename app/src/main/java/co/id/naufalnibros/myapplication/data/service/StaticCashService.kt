package co.id.naufalnibros.myapplication.data.service

import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.Flowable
import retrofit2.http.GET

interface StaticCashService {

    @GET("/static-cash.json")
    fun list(): Flowable<Map<String, List<Item>>>

}