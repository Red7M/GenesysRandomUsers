package com.genesys.challenge.redamehali.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.genesys.challenge.redamehali.R

/**
 * Fragment responsible for displaying countries represented and users count per country in
 * a center Custom Dialog.
 */
class StatsDialogFragment(private val countriesUsersStats: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(countriesUsersStats)
            .setPositiveButton(getString(R.string.ok)) {_, _ -> }
            .create()
    }

    companion object {
        const val TAG = "StatsDialogFragment"
    }
}