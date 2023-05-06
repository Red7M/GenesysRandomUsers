package com.genesys.challenge.redamehali.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.genesys.challenge.redamehali.R
import com.genesys.challenge.redamehali.activities.MainActivity
import com.genesys.challenge.redamehali.commons.InputTypeEnum
import com.genesys.challenge.redamehali.databinding.DefaultDialogBinding
import com.genesys.challenge.redamehali.interfaces.InputListener

/**
 * Fragment dialog responsible of taking user inputs for Seed and Result fetch count
 */
class SetDialogFragment(
    private val title: String,
    private val hint: String,
    private val inputListener: InputListener,
    private val inputType: InputTypeEnum
) : DialogFragment() {

    private var _binding : DefaultDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = DefaultDialogBinding.inflate(requireActivity().layoutInflater)
            setViews()

            AlertDialog.Builder(requireActivity())
                .setView(binding.root)
                .setPositiveButton(R.string.ok) { _, _ ->
                    val input = binding.dialogEditText.text.toString()
                    if (input.isBlank()) return@setPositiveButton

                    // If results fetch count greater than 2000, fetch count will NOT be edited.
                    if (inputType == InputTypeEnum.FETCH_COUNT_INPUT && input.toInt() > 2000) {
                        Toast.makeText(requireContext(), resources.getString(R.string.count_cannot_be_more_than_two_thousand_results), Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    // Listener to inform its implementer about the changes (seed or result fetch count)
                    inputListener.onInputSet(binding.dialogEditText.text.toString(), inputType)
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.cancel()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Sets view of the Dialog Fragment based on the selected option from the item menu in
     * the [MainActivity]
     */
    private fun setViews() {
        binding.dialogTitle.text = title
        binding.dialogEditText.hint = hint

        if (inputType == InputTypeEnum.SEED_INPUT) {
            binding.dialogEditText.inputType = InputType.TYPE_CLASS_TEXT
        } else if (inputType == InputTypeEnum.FETCH_COUNT_INPUT) {
            binding.dialogEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}