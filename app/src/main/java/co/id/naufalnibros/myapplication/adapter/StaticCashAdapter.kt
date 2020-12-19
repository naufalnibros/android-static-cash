package co.id.naufalnibros.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.id.naufalnibros.myapplication.data.response.Item
import co.id.naufalnibros.myapplication.databinding.RecyclerviewItemBinding
import co.id.naufalnibros.myapplication.ui.home.HomeFragmentDirections
import java.util.*
import kotlin.collections.ArrayList

class StaticCashAdapter(
    private val onSaveListener: OnSaveListener,
    private val onDeleteListener: OnDeleteListener
) : RecyclerView.Adapter<StaticCashAdapter.ItemViewHolder>() {

    val list = ArrayList<Item>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder(
        RecyclerviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.txtDate.text = list[position].date
        holder.binding.txtLabel.text = list[position].label
        holder.binding.txtVisit.text =
            String.format(Locale.getDefault(), "Visits %d", list[position].nb_visits)
        holder.binding.txtStatus.text = if (list[position].status) "Saved" else "Not Saved"
        holder.binding.btnSave.text = if (list[position].status) "DELETE" else "SAVED"
        holder.binding.btnSave.setBackgroundColor(
            if (list[position].status) Color.RED else Color.BLUE
        )

        holder.binding.btnSave.setOnClickListener {
            if (list[position].status)
                onDeleteListener.deleteItem(list[position], position)
            else
                onSaveListener.saveItem(list[position], position)
        }

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    list[position]
                )
            )
        }
    }

    interface OnDeleteListener {
        fun deleteItem(item: Item, position: Int)
    }

    interface OnSaveListener {
        fun saveItem(item: Item, position: Int)
    }

    override fun getItemCount() = list.size

    class ItemViewHolder(val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}