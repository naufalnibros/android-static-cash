package co.id.naufalnibros.myapplication.injection

import android.content.Context
import co.id.naufalnibros.myapplication.ui.main.MainRepository

object InjectionRepository {

    fun provideStaticCash(contex: Context): MainRepository {
        return MainRepository.getInstance(
            InjectionService.provideStaticCashService(),
            InjectionDao.provideStaticCasshDao(contex)
        )
    }

}