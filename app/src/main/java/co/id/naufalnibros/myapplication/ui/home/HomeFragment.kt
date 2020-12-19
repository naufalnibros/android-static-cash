package co.id.naufalnibros.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.id.naufalnibros.myapplication.R
import co.id.naufalnibros.myapplication.adapter.StaticCashAdapter
import co.id.naufalnibros.myapplication.data.Status
import co.id.naufalnibros.myapplication.data.response.Item
import co.id.naufalnibros.myapplication.databinding.FragmentHomeBinding
import co.id.naufalnibros.myapplication.injection.InjectionRepository
import co.id.naufalnibros.myapplication.utils.viewBinding

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    StaticCashAdapter.OnSaveListener, StaticCashAdapter.OnDeleteListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private var adapter = StaticCashAdapter(this, this)

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(
            InjectionRepository
                .provideStaticCash(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(this)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.nav_reset -> viewModel.deleteAll()
            }
            true
        }

        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.liveDataList.observe(viewLifecycleOwner, {
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
                            "subscribeUi: viewModel.liveDataList.observe ${it.message}"
                        )
                    }
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
            }
        })

        viewModel.liveDataSave.observe(viewLifecycleOwner, { it ->
            when (it) {
                HomeViewModel.HomeState.OnLoading -> {
                }
                is HomeViewModel.HomeState.OnSuccess -> {
                    it.position?.let {
                        adapter.list[it].status = true
                        adapter.notifyItemChanged(it)
                    }
                }
                is HomeViewModel.HomeState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "subscribeUi: viewModel.liveDataSave.observe ${it.message}"
                    )
                }
            }
        })

        viewModel.liveDataDelete.observe(viewLifecycleOwner, { it ->
            when (it) {
                HomeViewModel.HomeState.OnLoading -> {
                }
                is HomeViewModel.HomeState.OnSuccess -> {
                    it.position?.let {
                        adapter.list[it].status = false
                        adapter.notifyItemChanged(it)
                    }
                }
                is HomeViewModel.HomeState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "subscribeUi: viewModel.liveDataDelete.observe ${it.message}"
                    )
                }
            }
        })

        viewModel.liveDataisSave.observe(viewLifecycleOwner, { it ->
            when (it) {
                HomeViewModel.HomeState.OnLoading -> {
                }
                is HomeViewModel.HomeState.OnSuccess -> {
                    Log.d(
                        javaClass.simpleName,
                        "find: subscribeUi: viewModel.liveDataisSave.observe ${it.position}"
                    )
                    it.position?.let {
                        adapter.list[it].status = true
                        adapter.notifyItemChanged(it)
                    }
                }
                is HomeViewModel.HomeState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "find: subscribeUi: viewModel.liveDataisSave.observe ${it.message}"
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
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