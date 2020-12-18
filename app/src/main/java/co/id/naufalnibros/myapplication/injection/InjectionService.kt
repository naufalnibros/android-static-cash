package co.id.naufalnibros.myapplication.injection

import co.id.naufalnibros.myapplication.data.network.NetworkModule
import co.id.naufalnibros.myapplication.data.service.StaticCashService

object InjectionService {
    private val network = NetworkModule.provideRetrofit(
        NetworkModule.provideHttpClient()
    )

    fun provideStaticCashService(): StaticCashService {
        return NetworkModule.provideService(network)
    }

}