package co.id.naufalnibros.myapplication.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.id.naufalnibros.myapplication.data.response.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repository: DetailRepository) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val stateisSave = MutableLiveData<DetailState>()

    val liveDataisSave get() = stateisSave

    fun save(item: Item) =
        disposable.add(
            repository.save(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { stateisSave.postValue(DetailState.OnDeleted) }
                .subscribe({
                    stateisSave.postValue(DetailState.OnSaved)
                    find(item)
                }, {
                    stateisSave.postValue(it.message?.let { error -> DetailState.OnError(error) })
                })
        )

    fun delete(item: Item) = disposable.add(
        repository.delete(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateisSave.postValue(DetailState.OnSaved)
            }
            .subscribe({
                stateisSave.postValue(DetailState.OnDeleted)
                find(item)
            }, {
                stateisSave.postValue(it.message?.let { error -> DetailState.OnError(error) })
            })
    )

    fun find(item: Item) = disposable.add(
        repository.find(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateisSave.postValue(DetailState.OnDeleted)
            }
            .subscribe({
                stateisSave.postValue(DetailState.OnSaved)
            }, {
                stateisSave.postValue((it.message?.let { error -> DetailState.OnError(error) }))
            })
    )

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    sealed class DetailState {
        object OnDeleted : DetailState()
        object OnSaved : DetailState()
        data class OnError(val message: String) : DetailState()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repository: DetailRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return DetailViewModel(repository) as T
                }
            }
        }
    }

}