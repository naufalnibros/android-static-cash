package co.id.naufalnibros.myapplication.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import co.id.naufalnibros.myapplication.R
import co.id.naufalnibros.myapplication.databinding.FragmentDetailBinding
import co.id.naufalnibros.myapplication.injection.InjectionRepository
import co.id.naufalnibros.myapplication.utils.viewBinding
import java.util.*

class DetailFragment : Fragment() {

    private val binding by viewBinding(FragmentDetailBinding::bind)

    private val args: DetailFragmentArgs by navArgs()

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModel.provideFactory(
            InjectionRepository.provideStaticCashDetail(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { view.findNavController().navigateUp() }

        binding.txtDate.text = args.item.date
        binding.txtLabel.text = args.item.label
        binding.txtVisit.text = String.format(Locale.getDefault(), "Visits %d", args.item.nb_visits)

        viewModel.find(args.item)

        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.liveDataisSave.observe(viewLifecycleOwner, {
            when (it) {
                DetailViewModel.DetailState.OnDeleted -> {
                    binding.txtStatus.text = getString(R.string.txt_not_saved)
                    binding.btnSave.text = getString(R.string.txt_action_save)
                    binding.btnSave.setBackgroundColor(Color.BLUE)
                    binding.btnSave.setOnClickListener { viewModel.save(args.item) }
                }
                is DetailViewModel.DetailState.OnSaved -> {
                    binding.txtStatus.text = getString(R.string.txt_saved)
                    binding.btnSave.text = getString(R.string.txt_action_delete)
                    binding.btnSave.setBackgroundColor(Color.RED)
                    binding.btnSave.setOnClickListener { viewModel.delete(args.item) }
                }
                is DetailViewModel.DetailState.OnError -> {
                    Log.d(
                        javaClass.simpleName,
                        "find: subscribeUi: viewModel.liveDataisSave.observe ${it.message}"
                    )
                }
            }
        })
    }
}