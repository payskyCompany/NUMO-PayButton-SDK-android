package io.paysky.paybutton.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager


fun hideSoftKeyboard(activity: Activity) {
    try {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        // Get the currently focused view
        val currentFocus = activity.currentFocus
        
        // Only hide keyboard if there's a focused view with a window token
        if (currentFocus != null && currentFocus.windowToken != null) {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus.windowToken,
                0
            )
        } else {
            // If no focused view, try to hide using the window's decor view
            val view = activity.window?.decorView
            if (view != null && view.windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )
            }
        }
    } catch (e: Exception) {
        // Silently handle any exceptions (e.g., if activity is finishing)
        // This prevents crashes when the keyboard is already hidden or activity is destroyed
    }
}


