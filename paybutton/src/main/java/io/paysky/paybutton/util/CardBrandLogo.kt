package io.paysky.paybutton.util

import io.paysky.paybutton.R
import io.paysky.paybutton.data.model.response.CardItem

/**
 * Maps a saved card to its brand logo drawable.
 * Shared by the saved-cards list and manage-cards screens so both resolve
 * logos identically.
 */
object CardBrandLogo {

    /**
     * Get the card image resource based on card brand or masked card number.
     *
     * Priority:
     * 1. Check cardItem.brand (from API) - most reliable
     * 2. Fallback to masked card number pattern matching
     *
     * @return Drawable resource ID for the card image ([R.drawable.ic_card]
     * when the brand cannot be determined).
     */
    fun forCard(cardItem: CardItem): Int {
        // First, try to determine from brand field (most reliable)
        val brand = cardItem.brand?.trim()?.lowercase()
        when {
            brand?.contains("visa") == true -> return R.drawable.visa_logo
            brand?.contains("mastercard") == true || brand?.contains("master") == true -> return R.drawable.master_logo
            brand?.contains("moamlaat") == true || brand?.contains("moamlat") == true -> return R.drawable.nomo
        }

        // Fallback: Check masked card number patterns
        val maskedNumber = cardItem.maskedCardNumber.trim()
        val digitsOnly = maskedNumber.filter { it.isDigit() }

        // Visa: starts with 4
        if (digitsOnly.startsWith("4") || maskedNumber.contains("****") && maskedNumber.endsWith("4")) {
            return R.drawable.visa_logo
        }

        // Mastercard: starts with 5 (51-55) or 2 (2221-2720)
        if (digitsOnly.startsWith("5") && digitsOnly.length >= 2) {
            val firstTwo = digitsOnly.take(2)
            if (firstTwo in "51".."55") {
                return R.drawable.master_logo
            }
        }
        if (digitsOnly.startsWith("2") && digitsOnly.length >= 4) {
            val firstFour = digitsOnly.take(4)
            if (firstFour in "2221".."2720") {
                return R.drawable.master_logo
            }
        }

        // Moamlaat: Check for specific patterns (e.g., starts with 63 or 87)
        if (digitsOnly.startsWith("63") || digitsOnly.startsWith("87")) {
            return R.drawable.nomo
        }

        // Default card icon
        return R.drawable.ic_card
    }
}
