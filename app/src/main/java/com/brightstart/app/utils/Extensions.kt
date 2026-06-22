package com.brightstart.app.utils

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.NumberFormat
import java.util.Locale

fun View.visible()   { visibility = View.VISIBLE }
fun View.gone()      { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun Fragment.toast(msg: String) =
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

fun Double.toRandFormat(): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    return fmt.format(this)
}

