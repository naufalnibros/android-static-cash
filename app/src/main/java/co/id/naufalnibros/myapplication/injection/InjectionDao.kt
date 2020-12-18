package co.id.naufalnibros.myapplication.injection

import android.content.Context
import co.id.naufalnibros.myapplication.data.local.LocalDatabase
import co.id.naufalnibros.myapplication.data.local.dao.StaticCashDao

object InjectionDao {

    fun provideStaticCasshDao(contex: Context): StaticCashDao {
        return LocalDatabase.getInstance(contex)!!.savedDao()
    }

}