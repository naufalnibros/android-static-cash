package co.id.naufalnibros.myapplication.ui.main

import androidx.lifecycle.*
import co.id.naufalnibros.myapplication.data.Resource
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(val repository: MainRepository) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val stateSave = MutableLiveData<MainState>()

    private val stateDelete = MutableLiveData<MainState>()

    private val stateRefresh = MutableLiveData<MainState>()

    private val stateisSave = MutableLiveData<MainState>()

    val liveDataSave get() = stateSave

    val liveDataDelete get() = stateDelete

    val liveDataisSave get() = stateisSave

    val liveDataList: LiveData<Resource<List<Item>>>

    init {
        liveDataList = Transformations.switchMap(stateRefresh) {
            when (it) {
                is MainState.OnSuccess -> repository.list()
                else -> MutableLiveData(
                    Resource(
                        status = Status.ERROR,
                        message = "Terjadi Kesalahan"
                    )
                )
            }
        }

        stateRefresh.postValue(MainState.OnSuccess())
    }

    fun refresh() = stateRefresh.postValue(MainState.OnSuccess())

    fun save(item: Item, position: Int) =
        disposable.add(
            repository.save(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { stateSave.postValue(MainState.OnLoading) }
                .subscribe({
                    stateSave.postValue(MainState.OnSuccess(position = position))
                }, {
                    stateSave.postValue(it.message?.let { error -> MainState.OnError(error) })
                })
        )

    fun delete(item: Item, position: Int) = disposable.add(
        repository.delete(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateDelete.postValue(MainState.OnLoading)
            }
            .subscribe({
                stateDelete.postValue(MainState.OnSuccess(position = position))
            }, {
                stateDelete.postValue(it.message?.let { error -> MainState.OnError(error) })
            })
    )

    fun find(item: Item, position: Int) = disposable.add(
        repository.find(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateisSave.value = MainState.OnLoading
            }
            .subscribe({
                stateisSave.value = MainState.OnSuccess(position = position)
            }, {
                stateisSave.value = (it.message?.let { error -> MainState.OnError(error) })
            })
    )

    fun deleteAll() = disposable.add(
        repository.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateRefresh.postValue(MainState.OnLoading)
            }
            .subscribe({
                stateRefresh.postValue(MainState.OnSuccess())
            }, {
                stateisSave.value = (it.message?.let { error -> MainState.OnError(error) })
            })
    )

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    sealed class MainState {
        object OnLoading : MainState()
        data class OnSuccess(val message: String? = null, val position: Int? = null) : MainState()
        data class OnError(val message: String) : MainState()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repository: MainRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return MainViewModel(repository) as T
                }
            }
        }
    }

}