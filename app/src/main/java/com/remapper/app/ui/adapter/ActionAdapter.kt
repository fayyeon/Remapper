package com.remapper.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.remapper.app.R
import com.remapper.app.data.ActionBadge
import com.remapper.app.data.RemapAction
import com.remapper.app.databinding.ItemActionRowBinding
import java.util.Locale

class ActionAdapter(
    private val selectedActionId: String?,
    private val onActionSelected: (RemapAction) -> Unit
) : RecyclerView.Adapter<ActionAdapter.ActionViewHolder>() {

    private val allActions = mutableListOf<RemapAction>()
    private val displayActions = mutableListOf<RemapAction>()

    fun setActions(actions: List<RemapAction>) {
        allActions.clear()
        allActions.addAll(actions)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase(Locale.ROOT)
        displayActions.clear()
        if (q.isEmpty()) {
            displayActions.addAll(allActions)
        } else {
            displayActions.addAll(allActions.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                        it.description.lowercase(Locale.ROOT).contains(q)
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemActionRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(displayActions[position])
    }

    override fun getItemCount(): Int = displayActions.size

    inner class ActionViewHolder(
        private val binding: ItemActionRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(action: RemapAction) {
            val context = binding.root.context
            binding.tvActionRowTitle.text = action.title
            binding.tvActionRowDesc.text = action.description
            binding.ivActionRowIcon.setImageResource(action.iconRes)

            binding.tvActionBadge.text = when (action.badge) {
                ActionBadge.DIRECT -> context.getString(R.string.badge_direct)
                ActionBadge.ADB_DIRECT -> context.getString(R.string.badge_adb_direct)
                ActionBadge.PANEL -> context.getString(R.string.badge_panel)
            }

            val isSelected = action.id == selectedActionId
            binding.ivSelectedCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onActionSelected(action)
            }
        }
    }
}
