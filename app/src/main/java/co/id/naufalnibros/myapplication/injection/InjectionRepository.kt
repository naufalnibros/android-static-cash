package co.id.naufalnibros.myapplication.injection

import android.content.Context
import co.id.naufalnibros.myapplication.ui.detail.DetailRepository
import co.id.naufalnibros.myapplication.ui.home.HomeRepository

object InjectionRepository {

    fun provideStaticCash(contex: Context): HomeRepository {
        return HomeRepository.getInstance(
            InjectionService.provideStaticCashService(),
            InjectionDao.provideStaticCasshDao(contex)
        )
    }

    fun provideStaticCashDetail(contex: Context) = DetailRepository.getInstance(
        InjectionDao.provideStaticCasshDao(contex)
    )

}