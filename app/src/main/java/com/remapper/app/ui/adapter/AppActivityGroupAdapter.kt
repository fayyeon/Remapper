package com.remapper.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.remapper.app.R
import com.remapper.app.data.ActionBadge
import com.remapper.app.data.ActionType
import com.remapper.app.data.ActivityActionItem
import com.remapper.app.data.AppActivityGroup
import com.remapper.app.data.RemapAction
import com.remapper.app.databinding.ItemActivitySubrowBinding
import com.remapper.app.databinding.ItemAppActivityGroupBinding
import java.util.Locale

class AppActivityGroupAdapter(
    private val onActivitySelected: (RemapAction) -> Unit
) : RecyclerView.Adapter<AppActivityGroupAdapter.GroupViewHolder>() {

    private val allGroups = mutableListOf<AppActivityGroup>()
    private val displayGroups = mutableListOf<AppActivityGroup>()

    fun setGroups(groups: List<AppActivityGroup>) {
        allGroups.clear()
        allGroups.addAll(groups)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase(Locale.ROOT)
        displayGroups.clear()

        if (q.isEmpty()) {
            displayGroups.addAll(allGroups)
        } else {
            for (group in allGroups) {
                val appMatches = group.appName.lowercase(Locale.ROOT).contains(q) ||
                        group.packageName.lowercase(Locale.ROOT).contains(q)

                val matchingActivities = group.activities.filter {
                    it.title.lowercase(Locale.ROOT).contains(q) ||
                            it.className.lowercase(Locale.ROOT).contains(q)
                }

                if (appMatches || matchingActivities.isNotEmpty()) {
                    displayGroups.add(
                        group.copy(
                            activities = if (matchingActivities.isNotEmpty()) matchingActivities else group.activities,
                            isExpanded = true // Auto-expand when searching
                        )
                    )
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemAppActivityGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(displayGroups[position])
    }

    override fun getItemCount(): Int = displayGroups.size

    inner class GroupViewHolder(
        private val binding: ItemAppActivityGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: AppActivityGroup) {
            val context = binding.root.context

            binding.tvGroupAppName.text = group.appName
            binding.tvGroupActivityCount.text = context.getString(R.string.activities_count, group.activities.size)

            if (group.appIcon != null) {
                binding.ivGroupAppIcon.setImageDrawable(group.appIcon)
            } else {
                binding.ivGroupAppIcon.setImageResource(R.drawable.ic_action_app)
            }

            updateExpandedState(group)

            binding.layoutAppHeader.setOnClickListener {
                group.isExpanded = !group.isExpanded
                updateExpandedState(group)
            }
        }

        private fun updateExpandedState(group: AppActivityGroup) {
            val inflater = LayoutInflater.from(binding.root.context)

            if (group.isExpanded) {
                binding.ivExpandArrow.setImageResource(R.drawable.ic_expand_less)
                binding.viewHeaderDivider.visibility = View.VISIBLE
                binding.layoutActivitiesContainer.visibility = View.VISIBLE
                binding.layoutActivitiesContainer.removeAllViews()

                for (activity in group.activities) {
                    val subBinding = ItemActivitySubrowBinding.inflate(
                        inflater,
                        binding.layoutActivitiesContainer,
                        false
                    )

                    subBinding.tvSubrowTitle.text = activity.title
                    subBinding.tvSubrowClassName.text = activity.className

                    if (group.appIcon != null) {
                        subBinding.ivSubrowIcon.setImageDrawable(group.appIcon)
                    } else {
                        subBinding.ivSubrowIcon.setImageResource(R.drawable.ic_action_shortcut)
                    }

                    subBinding.root.setOnClickListener {
                        val action = RemapAction(
                            id = "activity:${activity.packageName}/${activity.className}",
                            title = "${group.appName}: ${activity.title}",
                            description = activity.className,
                            type = ActionType.APP_SHORTCUT,
                            packageName = activity.packageName,
                            className = activity.className,
                            iconRes = R.drawable.ic_action_shortcut,
                            badge = ActionBadge.DIRECT
                        )
                        onActivitySelected(action)
                    }

                    binding.layoutActivitiesContainer.addView(subBinding.root)
                }
            } else {
                binding.ivExpandArrow.setImageResource(R.drawable.ic_expand_more)
                binding.viewHeaderDivider.visibility = View.GONE
                binding.layoutActivitiesContainer.visibility = View.GONE
                binding.layoutActivitiesContainer.removeAllViews()
            }
        }
    }
}
