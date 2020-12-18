package co.id.naufalnibros.myapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.id.naufalnibros.myapplication.R
import co.id.naufalnibros.myapplication.adapter.StaticCashAdapter
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.response.Item
import co.id.naufalnibros.myapplication.databinding.ActivityMainBinding
import co.id.naufalnibros.myapplication.injection.InjectionRepository
import co.id.naufalnibros.myapplication.utils.viewBinding

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    StaticCashAdapter.OnSaveListener, StaticCashAdapter.OnDeleteListener {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.provideFactory(
            InjectionRepository
                .provideStaticCash(contex = applicationContext)
        )
    }

    private var adapter = StaticCashAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerview.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(this)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.nav_reset -> viewModel.deleteAll()
            }
            true
        }

        viewModel.liveDataList.observe(this, {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresh.isRefreshing = false
                        adapter.list.clear()
                        adapter.list.addAll(it.data!!)
                        adapter.notifyDataSetChanged()
                        adapter.list.forEachIndexed { position, item ->
                            viewModel.find(
                                item,
                                position
                            )
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefresh.isRefreshing = false
                        Log.d(
                            javaClass.simpleName,
                            "onCreate: viewModel.liveDataList.observe ${it.message}"
                        )
                    }
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
            }
        })

        viewModel.liveDataSave.observe(this, { it ->
            when (it) {
                MainViewModel.MainState.OnLoading -> {
                }
                is MainViewModel.MainState.OnSuccess -> {
                    it.position?.let {
                        adapter.list[it].status = true
                        adapter.notifyItemChanged(it)
                    }
                }
                is MainViewModel.MainState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "onCreate: viewModel.liveDataSave.observe ${it.message}"
                    )
                }
            }
        })

        viewModel.liveDataDelete.observe(this, { it ->
            when (it) {
                MainViewModel.MainState.OnLoading -> {
                }
                is MainViewModel.MainState.OnSuccess -> {
                    it.position?.let {
                        adapter.list[it].status = false
                        adapter.notifyItemChanged(it)
                    }
                }
                is MainViewModel.MainState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "onCreate: viewModel.liveDataDelete.observe ${it.message}"
                    )
                }
            }
        })

        viewModel.liveDataisSave.observe(this, { it ->
            when (it) {
                MainViewModel.MainState.OnLoading -> {
                }
                is MainViewModel.MainState.OnSuccess -> {
                    Log.d(
                        javaClass.simpleName,
                        "find: onCreate: viewModel.liveDataisSave.observe ${it.position}"
                    )
                    it.position?.let {
                        adapter.list[it].status = true
                        adapter.notifyItemChanged(it)
                    }
                }
                is MainViewModel.MainState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "onCreate: viewModel.liveDataisSave.observe ${it.message}"
                    )
                }
            }
        })

    }

    override fun onRefresh() {
        viewModel.refresh()
    }

    override fun saveItem(item: Item, position: Int) {
        viewModel.save(item, position)
    }

    override fun deleteItem(item: Item, position: Int) {
        viewModel.delete(item, position)
    }

}