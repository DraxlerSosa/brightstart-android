package com.brightstart.app.utils

import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.NumberFormat
import java.util.Locale

fun View.visible()   { visibility = View.VISIBLE }
fun View.gone()      { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun Fragment.toast(msg: String) =
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

/**
 * Shows the dialog with a positive button that only closes it when [validate]
 * returns null. When validation fails the dialog stays open and the message is
 * shown, so the user can fix the input instead of losing what they typed.
 */
fun AlertDialog.Builder.showValidated(
    positiveText: String,
    validate: () -> String?,
    onValid: () -> Unit
): AlertDialog {
    setPositiveButton(positiveText, null)
    setNegativeButton("Cancel", null)
    val dialog = create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val error = validate()
            if (error != null) {
                Toast.makeText(dialog.context, error, Toast.LENGTH_LONG).show()
            } else {
                onValid()
                dialog.dismiss()
            }
        }
    }
    dialog.show()
    return dialog
}

fun Double.toRandFormat(): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    return fmt.format(this)
}

