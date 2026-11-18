package com.remapper.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.remapper.app.R
import com.remapper.app.data.PreferencesManager
import com.remapper.app.data.TriggerType
import com.remapper.app.databinding.ItemTriggerCardBinding

class TriggerAdapter(
    private val prefsManager: PreferencesManager,
    private val onPickAction: (TriggerType) -> Unit,
    private val onToggleTrigger: (TriggerType, Boolean) -> Unit
) : RecyclerView.Adapter<TriggerAdapter.TriggerViewHolder>() {

    private val triggers = mutableListOf<TriggerType>()

    fun setTriggers(newTriggers: List<TriggerType>) {
        triggers.clear()
        triggers.addAll(newTriggers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TriggerViewHolder {
        val binding = ItemTriggerCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TriggerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TriggerViewHolder, position: Int) {
        holder.bind(triggers[position])
    }

    override fun getItemCount(): Int = triggers.size

    inner class TriggerViewHolder(
        private val binding: ItemTriggerCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trigger: TriggerType) {
            val context = binding.root.context
            binding.tvTriggerTitle.setText(trigger.titleRes)
            binding.tvTriggerDesc.setText(trigger.descRes)
            binding.ivTriggerIcon.setImageResource(trigger.iconRes)

            val isEnabled = prefsManager.isTriggerEnabled(trigger)
            binding.switchTrigger.setOnCheckedChangeListener(null)
            binding.switchTrigger.isChecked = isEnabled
            binding.switchTrigger.setOnCheckedChangeListener { _, checked ->
                onToggleTrigger(trigger, checked)
            }

            val action = prefsManager.getActionForTrigger(trigger)
            if (action != null) {
                binding.tvActionName.text = action.title
                binding.ivActionIcon.setImageResource(action.iconRes)
            } else {
                binding.tvActionName.text = context.getString(R.string.no_action_assigned)
                binding.ivActionIcon.setImageResource(R.drawable.ic_action_shortcut)
            }

            binding.cardTrigger.setOnClickListener {
                onPickAction(trigger)
            }

            binding.btnPickAction.setOnClickListener {
                onPickAction(trigger)
            }
        }
    }
}
