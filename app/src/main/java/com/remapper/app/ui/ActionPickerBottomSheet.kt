package com.remapper.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.remapper.app.R
import com.remapper.app.data.ActionRepository
import com.remapper.app.data.PreferencesManager
import com.remapper.app.data.RemapAction
import com.remapper.app.data.TriggerType
import com.remapper.app.databinding.BottomSheetActionPickerBinding
import com.remapper.app.ui.adapter.ActionAdapter
import com.remapper.app.ui.adapter.AppAdapter
import com.remapper.app.ui.adapter.AppActivityGroupAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActionPickerBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetActionPickerBinding? = null
    private val binding get() = _binding!!

    private var trigger: TriggerType = TriggerType.HOLD_HOME
    private var onActionPickedListener: ((RemapAction?) -> Unit)? = null

    private lateinit var actionAdapter: ActionAdapter
    private lateinit var appAdapter: AppAdapter
    private lateinit var activityGroupAdapter: AppActivityGroupAdapter

    private var currentTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetActionPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val triggerKey = arguments?.getString(ARG_TRIGGER_KEY)
        trigger = TriggerType.fromKey(triggerKey ?: "") ?: TriggerType.HOLD_HOME

        binding.tvPickerSubtitle.text = getString(R.string.subtitle_triggers) + ": " + getString(trigger.titleRes)

        val prefs = PreferencesManager.getInstance(requireContext())
        val currentAction = prefs.getActionForTrigger(trigger)

        // Initialize Adapters
        actionAdapter = ActionAdapter(currentAction?.id) { selected ->
            onActionPickedListener?.invoke(selected)
            dismiss()
        }

        appAdapter = AppAdapter(currentAction?.id) { selected ->
            onActionPickedListener?.invoke(selected)
            dismiss()
        }

        activityGroupAdapter = AppActivityGroupAdapter { selected ->
            onActionPickedListener?.invoke(selected)
            dismiss()
        }

        binding.rcvActionOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvActionOptions.adapter = actionAdapter

        // Load Quick Panel actions (Alphabetically sorted)
        val quickActions = ActionRepository.getQuickPanelActions(requireContext())
        actionAdapter.setActions(quickActions)

        // Async load installed apps (Alphabetically sorted)
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val apps = ActionRepository.getInstalledApps(requireContext())
            val activityGroups = ActionRepository.getAllAppActivities(requireContext())
            withContext(Dispatchers.Main) {
                appAdapter.setApps(apps)
                activityGroupAdapter.setGroups(activityGroups)
                _binding?.progressBar?.visibility = View.GONE
            }
        }

        // Tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                val query = binding.etSearch.text?.toString() ?: ""
                when (currentTab) {
                    0 -> {
                        binding.rcvActionOptions.adapter = actionAdapter
                        actionAdapter.filter(query)
                    }
                    1 -> {
                        binding.rcvActionOptions.adapter = appAdapter
                        appAdapter.filter(query)
                    }
                    2 -> {
                        binding.rcvActionOptions.adapter = activityGroupAdapter
                        activityGroupAdapter.filter(query)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Search text watcher
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString() ?: ""
                when (currentTab) {
                    0 -> actionAdapter.filter(q)
                    1 -> appAdapter.filter(q)
                    2 -> activityGroupAdapter.filter(q)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Clear button
        binding.btnClearAction.setOnClickListener {
            onActionPickedListener?.invoke(null)
            dismiss()
        }
    }

    fun setOnActionPickedListener(listener: (RemapAction?) -> Unit) {
        this.onActionPickedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRIGGER_KEY = "arg_trigger_key"

        fun newInstance(trigger: TriggerType): ActionPickerBottomSheet {
            return ActionPickerBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TRIGGER_KEY, trigger.key)
                }
            }
        }
    }
}
