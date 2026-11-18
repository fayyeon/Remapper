package com.remapper.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.remapper.app.R
import com.remapper.app.databinding.BottomSheetAdbGuideBinding

class AdbGuideBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAdbGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAdbGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCopyAdbCommand.setOnClickListener {
            copyCommandToClipboard()
        }

        binding.tvAdbCommand.setOnClickListener {
            copyCommandToClipboard()
        }

        binding.btnCloseAdbGuide.setOnClickListener {
            dismiss()
        }
    }

    private fun copyCommandToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ADB Command", getString(R.string.adb_command))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): AdbGuideBottomSheet = AdbGuideBottomSheet()
    }
}
