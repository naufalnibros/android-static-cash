package co.id.naufalnibros.myapplication.ui.home

import androidx.lifecycle.*
import co.id.naufalnibros.myapplication.data.Resource
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val stateSave = MutableLiveData<HomeState>()

    private val stateDelete = MutableLiveData<HomeState>()

    private val stateRefresh = MutableLiveData<HomeState>()

    private val stateisSave = MutableLiveData<HomeState>()

    val liveDataSave get() = stateSave

    val liveDataDelete get() = stateDelete

    val liveDataisSave get() = stateisSave

    val liveDataList: LiveData<Resource<List<Item>>>

    init {
        liveDataList = Transformations.switchMap(stateRefresh) {
            when (it) {
                is HomeState.OnSuccess -> repository.list()
                else -> MutableLiveData(
                    Resource(
                        status = Status.ERROR,
                        message = "Terjadi Kesalahan"
                    )
                )
            }
        }

        stateRefresh.postValue(HomeState.OnSuccess())
    }

    fun refresh() = stateRefresh.postValue(HomeState.OnSuccess())

    fun save(item: Item, position: Int) =
        disposable.add(
            repository.save(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { stateSave.postValue(HomeState.OnLoading) }
                .subscribe({
                    stateSave.postValue(HomeState.OnSuccess(position = position))
                }, {
                    stateSave.postValue(it.message?.let { error -> HomeState.OnError(error) })
                })
        )

    fun delete(item: Item, position: Int) = disposable.add(
        repository.delete(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateDelete.postValue(HomeState.OnLoading)
            }
            .subscribe({
                stateDelete.postValue(HomeState.OnSuccess(position = position))
            }, {
                stateDelete.postValue(it.message?.let { error -> HomeState.OnError(error) })
            })
    )

    fun find(item: Item, position: Int) = disposable.add(
        repository.find(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateisSave.value = HomeState.OnLoading
            }
            .subscribe({
                stateisSave.value = HomeState.OnSuccess(position = position)
            }, {
                stateisSave.value = (it.message?.let { error -> HomeState.OnError(error) })
            })
    )

    fun deleteAll() = disposable.add(
        repository.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateRefresh.postValue(HomeState.OnLoading)
            }
            .subscribe({
                stateRefresh.postValue(HomeState.OnSuccess())
            }, {
                stateisSave.value = (it.message?.let { error -> HomeState.OnError(error) })
            })
    )

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    sealed class HomeState {
        object OnLoading : HomeState()
        data class OnSuccess(val message: String? = null, val position: Int? = null) : HomeState()
        data class OnError(val message: String) : HomeState()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repository: HomeRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
        }
    }
}