package com.remapper.app.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.remapper.app.R
import com.remapper.app.data.ActionType
import com.remapper.app.data.RemapAction
import com.remapper.app.databinding.ItemAppRowBinding
import java.util.Locale

class AppAdapter(
    private val selectedActionId: String?,
    private val onAppSelected: (RemapAction) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val allApps = mutableListOf<RemapAction>()
    private val displayApps = mutableListOf<RemapAction>()
    private val iconCache = mutableMapOf<String, Drawable>()

    fun setApps(apps: List<RemapAction>) {
        allApps.clear()
        allApps.addAll(apps)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase(Locale.ROOT)
        displayApps.clear()
        if (q.isEmpty()) {
            displayApps.addAll(allApps)
        } else {
            displayApps.addAll(allApps.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                        it.description.lowercase(Locale.ROOT).contains(q)
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(displayApps[position])
    }

    override fun getItemCount(): Int = displayApps.size

    inner class AppViewHolder(
        private val binding: ItemAppRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(action: RemapAction) {
            val context = binding.root.context
            val pm = context.packageManager
            binding.tvAppName.text = action.title
            binding.tvAppPackage.text = action.description

            val pkg = action.packageName
            if (pkg != null && action.type == ActionType.APP_LAUNCH) {
                val cached = iconCache[pkg]
                if (cached != null) {
                    binding.ivAppIcon.setImageDrawable(cached)
                } else {
                    try {
                        val icon = pm.getApplicationIcon(pkg)
                        iconCache[pkg] = icon
                        binding.ivAppIcon.setImageDrawable(icon)
                    } catch (_: Exception) {
                        binding.ivAppIcon.setImageResource(R.drawable.ic_action_app)
                    }
                }
            } else {
                binding.ivAppIcon.setImageResource(action.iconRes)
            }

            val isSelected = action.id == selectedActionId
            binding.ivAppSelectedCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onAppSelected(action)
            }
        }
    }
}
