package com.remapper.app.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.remapper.app.R
import com.remapper.app.data.PreferencesManager
import com.remapper.app.data.TriggerType
import com.remapper.app.databinding.ActivityMainBinding
import com.remapper.app.service.RemapperAccessibilityService
import com.remapper.app.ui.adapter.TriggerAdapter
import com.remapper.app.util.NavigationModeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PreferencesManager
    private lateinit var triggerAdapter: TriggerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge system insets handling
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        prefsManager = PreferencesManager.getInstance(this)

        setupServiceCard()
        setupTriggersList()
        setupPreferences()
        setupPermissionCards()
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        updatePermissionCards()
        // Re-evaluate navigation mode and refresh visible triggers
        val visibleTriggers = getVisibleTriggers()
        triggerAdapter.setTriggers(visibleTriggers)
    }

    private fun getVisibleTriggers(): List<TriggerType> {
        val is3Button = NavigationModeHelper.is3ButtonNavigation(this)
        return TriggerType.entries.filter { !it.is3ButtonNavOnly || is3Button }
    }

    private fun setupServiceCard() {
        binding.btnToggleService.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun updateServiceStatus() {
        val isEnabled = RemapperAccessibilityService.isAccessibilityServiceEnabled(this)
        if (isEnabled) {
            binding.tvServiceStatusTitle.setText(R.string.service_status_active)
            binding.tvServiceStatusDesc.setText(R.string.service_status_desc_active)
            binding.tvServiceStatusBadge.text = "Active"
            binding.tvServiceStatusBadge.setTextColor(getColor(R.color.status_active_text))
            binding.btnToggleService.setText(R.string.btn_service_active)
        } else {
            binding.tvServiceStatusTitle.setText(R.string.service_status_inactive)
            binding.tvServiceStatusDesc.setText(R.string.service_status_desc_inactive)
            binding.tvServiceStatusBadge.text = "Disabled"
            binding.tvServiceStatusBadge.setTextColor(getColor(R.color.status_inactive_text))
            binding.btnToggleService.setText(R.string.btn_enable_service)
        }
    }

    private fun setupTriggersList() {
        triggerAdapter = TriggerAdapter(
            prefsManager = prefsManager,
            onPickAction = { trigger ->
                openActionPicker(trigger)
            },
            onToggleTrigger = { trigger, enabled ->
                prefsManager.setTriggerEnabled(trigger, enabled)
            }
        )

        binding.rcvTriggers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = triggerAdapter
        }

        // Set initial visible triggers based on navigation mode
        triggerAdapter.setTriggers(getVisibleTriggers())
    }

    private fun openActionPicker(trigger: TriggerType) {
        val bottomSheet = ActionPickerBottomSheet.newInstance(trigger)
        bottomSheet.setOnActionPickedListener { action ->
            prefsManager.setActionForTrigger(trigger, action)
            triggerAdapter.notifyDataSetChanged()
        }
        bottomSheet.show(supportFragmentManager, "action_picker")
    }

    private fun setupPreferences() {
        binding.switchHaptic.isChecked = prefsManager.isHapticEnabled()
        binding.switchHaptic.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setHapticEnabled(isChecked)
        }

        binding.btnAdbGuide.setOnClickListener {
            AdbGuideBottomSheet.newInstance().show(supportFragmentManager, "adb_guide")
        }
    }

    private fun setupPermissionCards() {
        binding.btnGrantWriteSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        binding.btnGrantDndPolicy.setOnClickListener {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun updatePermissionCards() {
        val canWrite = Settings.System.canWrite(this)
        binding.btnGrantWriteSettings.text = if (canWrite) getString(R.string.granted) else getString(R.string.btn_grant)
        binding.btnGrantWriteSettings.isEnabled = !canWrite

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val dndGranted = nm.isNotificationPolicyAccessGranted
        binding.btnGrantDndPolicy.text = if (dndGranted) getString(R.string.granted) else getString(R.string.btn_grant)
        binding.btnGrantDndPolicy.isEnabled = !dndGranted
    }
}
