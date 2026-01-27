package io.paysky.paybutton.ui.fragment.listcards

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.paysky.paybutton.R
import io.paysky.paybutton.data.model.response.CardItem
import io.paysky.paybutton.ui.activity.payment.PaymentActivity
import io.paysky.paybutton.util.hideSoftKeyboard



class SavedCardsAdapter(
    val onSubmitDataValid: (CardItem) -> Unit,
    val onChangeItem: (Int) -> Unit,
    val activity: PaymentActivity
) : RecyclerView.Adapter<SavedCardsAdapter.SavedCardViewHolder>() {
    private val savedCardsList = mutableListOf<CardItem>()
    private var selectedItemPosition = -1
    private val handler = Handler(Looper.getMainLooper())
    
    /**
     * Safely notify item changed by deferring the call until after the current layout pass
     */
    private fun safeNotifyItemChanged(position: Int) {
        if (position >= 0 && position < savedCardsList.size) {
            handler.post {
                try {
                    notifyItemChanged(position)
                } catch (e: IllegalStateException) {
                    // If RecyclerView is still computing layout, try again after a short delay
                    handler.postDelayed({
                        try {
                            notifyItemChanged(position)
                        } catch (e2: Exception) {
                            // Ignore if still failing
                        }
                    }, 50)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_card, parent, false)

        return SavedCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return savedCardsList.size
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.setCardData(savedCardsList[position], position)
    }

    fun setItems(cardsLists: List<CardItem>) {
        val diffCallback = CardsCallback(this.savedCardsList, cardsLists)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        savedCardsList.clear()
        savedCardsList.addAll(cardsLists)
        diffResult.dispatchUpdatesTo(this)
    }

    fun submit() {
        if (selectedItemPosition != -1) {
            val selectedCard = savedCardsList[selectedItemPosition]
            
            // Check if CVV is required for this card
            if (isCvvRequired(selectedCard)) {
                // CVV is required - validate it
                if (selectedCard.cvv.isNullOrEmpty()) {
                    selectedCard.isError = true
                    safeNotifyItemChanged(selectedItemPosition)
                } else {
                    selectedCard.isError = false
                    onSubmitDataValid(selectedCard)
                    safeNotifyItemChanged(selectedItemPosition)
                }
            } else {
                // CVV is not required - proceed without CVV
                selectedCard.isError = false
                onSubmitDataValid(selectedCard)
                safeNotifyItemChanged(selectedItemPosition)
            }
        }
    }

    /**
     * Extract the actual card number length from masked card number.
     * This works with various masking formats from real APIs:
     * - "**** **** **** 1234" -> 16 digits (standard format)
     * - "**** ****** *3456" -> 19 digits (some formats)
     * - "1234 5678 9012 3456 789" -> 19 digits (full number shown)
     * - "****-****-****-1234" -> 16 digits (with dashes)
     * 
     * The function counts all digits in the string, regardless of separators.
     * For real APIs, the masked number typically shows the last 4 digits,
     * but we count all visible digits to determine the total card length.
     */
    private fun getCardNumberLength(maskedCardNumber: String): Int {
        if (maskedCardNumber.isBlank()) {
            return 0
        }
        
        // Count only digits in the masked card number, ignoring spaces, dashes, etc.
        val digitCount = maskedCardNumber.filter { it.isDigit() }.length
        
        // If the masked number shows all digits (unmasked), return the count directly
        // If it's masked (contains * or X), we need to infer the total length
        // For masked numbers, we typically see the last 4 digits, so:
        // - If we see 4 digits, it's likely a 16-digit card (standard)
        // - If we see more digits, count them
        // - For 19-digit cards, APIs might show more digits or use different format
        
        // Real API behavior: Most APIs mask with * and show last 4 digits
        // For 19-digit cards, the format might be different
        // We'll count all visible digits and use that as the indicator
        
        return digitCount
    }
    
    /**
     * Check if a card is a 19-digit card based on masked card number.
     * For real APIs, this works if:
     * 1. The API shows all 19 digits (unmasked)
     * 2. The API uses a format that shows 19 digits
     * 
     * If the API only shows last 4 digits in masked format, this won't work
     * and you may need to use card brand or another API field.
     * 
     * Note: If your API provides card length in a separate field or metadata,
     * you should use that instead. This function works when the masked number
     * contains 19 visible digits.
     */
    private fun is19DigitCard(cardItem: CardItem): Boolean {
        val digitCount = getCardNumberLength(cardItem.maskedCardNumber)
        
        // If we see exactly 19 digits, it's a 19-digit card
        if (digitCount == 19) {
            return true
        }
        
        // For masked numbers showing only last 4 digits, we can't determine
        // In this case, you might want to check the brand or use API metadata
        // For now, we only return true if we see 19 digits
        return false
    }
    
    /**
     * Determine if CVV is required for a card.
     * 
     * Rules:
     * - 19-digit cards: CVV is NOT required (hide CVV EditText)
     * - All other cards: CVV IS required (show CVV EditText)
     * 
     * You can extend this function to add more rules based on:
     * - Card brand (e.g., certain brands don't need CVV)
     * - Card type from API
     * - Any other API-provided metadata
     * 
     * @param cardItem The card item to check
     * @return true if CVV is required (show EditText), false if not required (hide EditText)
     */
    private fun isCvvRequired(cardItem: CardItem): Boolean {
        // Rule 1: 19-digit cards don't need CVV
        if (is19DigitCard(cardItem)|| cardItem.maskedCardNumber.take(2) == "63"
            ||cardItem.maskedCardNumber.take(2) == "87") {
            return false
        }
        
        // Rule 2: You can add more rules here based on:
        // - cardItem.brand (e.g., certain brands)
        // - cardItem.displayName
        // - Any other API field that indicates CVV requirement
        
        // Default: CVV is required for all other cards
        return true
    }

    inner class SavedCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var position: Int = -1
        private val cvv: EditText = itemView.findViewById(R.id.ccv_editText1)
        private val maskedCardNumber: TextView =
            itemView.findViewById(R.id.masked_card_number_text_view)
        private val cardName: TextView = itemView.findViewById(R.id.card_name_textview)
        private val selectCard: RadioButton = itemView.findViewById(R.id.card_selected_radio_button)
        private val cvvRequired: View = itemView.findViewById(R.id.cvv_required)

        fun setCardData(cardItem: CardItem, position: Int) {
            selectCard.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    updateSelectedCard(position)
                }
            }

            cvv.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    savedCardsList[position].cvv = s?.toString() ?: ""
                    if (!s.isNullOrEmpty()) {
                        savedCardsList[position].isError = false
                    }
                    updateCvvBackground(cardItem)
                }
            })

            this.position = position
            maskedCardNumber.text = cardItem.maskedCardNumber
            cardName.text = cardItem.displayName
            
            // Determine if CVV is required for this card
            val isCvvNeeded = isCvvRequired(cardItem)
            
            if (cardItem.isSelected) {
                selectedItemPosition = position
                selectCard.isChecked = true
                // Show CVV EditText only if CVV is required for this card
                if (isCvvNeeded) {
                    cvv.visibility = View.VISIBLE
                    cvv.setText(cardItem.cvv ?: "")
                } else {
                    // Hide CVV EditText if CVV is not required
                    cvv.visibility = View.GONE
                    cvv.setText("") // Clear any existing CVV value
                }
            } else {
                selectCard.isChecked = false
                cvv.visibility = View.INVISIBLE
                cvv.setText("")
            }
            cvv.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard(activity = activity)
                    true
                } else false
            }
        }

        private fun updateCvvBackground(cardItem: CardItem) {
            if (cardItem.isError) {
                cvv.setBackgroundResource(R.drawable.error_border_background)
                cvvRequired.visibility = View.VISIBLE
            } else {
                cvv.setBackgroundResource(R.drawable.cvv_border_background)
                cvvRequired.visibility = View.GONE
            }
        }
    }

    private fun updateSelectedCard(position: Int) {
        val previousPosition = selectedItemPosition
        
        if (previousPosition != -1) {
            savedCardsList[previousPosition] =
                savedCardsList[previousPosition].copy(isSelected = false, isError = false)
        }
        savedCardsList[position] =
            savedCardsList[position].copy(isSelected = true)

        this.selectedItemPosition = position

        // Notify changes through the callback which uses a Handler to defer
        // This prevents IllegalStateException when RecyclerView is computing layout
        onChangeItem(position)
        if (previousPosition != -1 && previousPosition != position) {
            onChangeItem(previousPosition)
        }
    }
}