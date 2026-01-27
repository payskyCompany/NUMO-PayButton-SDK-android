# CVV Visibility Handling Guide

This document explains how CVV EditText visibility is handled in the saved cards list screen.

## Overview

The CVV EditText is shown or hidden based on whether the card requires CVV for payment processing.

## How It Works

### 1. **CVV Requirement Check**

The `isCvvRequired()` function determines if a card needs CVV:

```kotlin
private fun isCvvRequired(cardItem: CardItem): Boolean {
    // Rule 1: 19-digit cards don't need CVV
    if (is19DigitCard(cardItem)) {
        return false
    }
    
    // Default: CVV is required for all other cards
    return true
}
```

### 2. **CVV Visibility Logic**

In `setCardData()` method:

```kotlin
// Determine if CVV is required for this card
val isCvvNeeded = isCvvRequired(cardItem)

if (cardItem.isSelected) {
    // Show CVV EditText only if CVV is required
    if (isCvvNeeded) {
        cvv.visibility = View.VISIBLE
        cvv.setText(cardItem.cvv ?: "")
    } else {
        // Hide CVV EditText if CVV is not required
        cvv.visibility = View.GONE
        cvv.setText("") // Clear any existing CVV value
    }
} else {
    // Card not selected - hide CVV
    cvv.visibility = View.INVISIBLE
    cvv.setText("")
}
```

### 3. **Validation on Submit**

In `submit()` method:

```kotlin
if (isCvvRequired(selectedCard)) {
    // CVV is required - validate it
    if (selectedCard.cvv.isNullOrEmpty()) {
        selectedCard.isError = true  // Show error
    } else {
        selectedCard.isError = false
        onSubmitDataValid(selectedCard)  // Proceed
    }
} else {
    // CVV is not required - proceed without CVV
    selectedCard.isError = false
    onSubmitDataValid(selectedCard)
}
```

## Current Rules

### ✅ **CVV NOT Required** (CVV EditText Hidden):
- **19-digit cards**: Cards with exactly 19 digits in the masked card number

### ✅ **CVV Required** (CVV EditText Shown):
- **All other cards**: 16-digit cards, 15-digit cards, etc.

## Extending the Rules

You can extend `isCvvRequired()` to add more rules based on:

### Example 1: Based on Card Brand
```kotlin
private fun isCvvRequired(cardItem: CardItem): Boolean {
    // Rule 1: 19-digit cards don't need CVV
    if (is19DigitCard(cardItem)) {
        return false
    }
    
    // Rule 2: Certain brands don't need CVV
    if (cardItem.brand?.equals("SomeBrand", ignoreCase = true) == true) {
        return false
    }
    
    // Default: CVV is required
    return true
}
```

### Example 2: Based on API Field (if available)
```kotlin
private fun isCvvRequired(cardItem: CardItem): Boolean {
    // If API provides a field indicating CVV requirement
    // if (cardItem.requiresCvv == false) {
    //     return false
    // }
    
    // Rule 1: 19-digit cards don't need CVV
    if (is19DigitCard(cardItem)) {
        return false
    }
    
    return true
}
```

## Visual States

### When Card is Selected:

| CVV Required? | EditText Visibility | User Action |
|--------------|---------------------|-------------|
| **Yes** | `View.VISIBLE` | User must enter CVV |
| **No** | `View.GONE` | CVV field is hidden |

### When Card is NOT Selected:

| State | EditText Visibility |
|-------|---------------------|
| Any card | `View.INVISIBLE` |

## Code Flow

```
User selects card
    ↓
setCardData() called
    ↓
isCvvRequired(cardItem) checked
    ↓
    ├─→ Returns true → Show CVV (View.VISIBLE)
    └─→ Returns false → Hide CVV (View.GONE)
    
User clicks "Proceed"
    ↓
submit() called
    ↓
isCvvRequired(selectedCard) checked
    ↓
    ├─→ Returns true → Validate CVV input
    │       ├─→ CVV empty → Show error
    │       └─→ CVV provided → Proceed
    └─→ Returns false → Proceed without CVV
```

## Testing

### Test Case 1: 19-Digit Card (CVV Not Required)
1. Select a 19-digit card
2. **Expected**: CVV EditText is hidden (`View.GONE`)
3. Click "Proceed"
4. **Expected**: Payment proceeds without CVV validation

### Test Case 2: Standard Card (CVV Required)
1. Select a 16-digit card
2. **Expected**: CVV EditText is visible (`View.VISIBLE`)
3. Click "Proceed" without entering CVV
4. **Expected**: Error shown, CVV required message displayed
5. Enter CVV and click "Proceed"
6. **Expected**: Payment proceeds with CVV

## Key Functions

- **`isCvvRequired(cardItem)`**: Determines if CVV is needed
- **`is19DigitCard(cardItem)`**: Checks if card has 19 digits
- **`setCardData()`**: Sets CVV visibility based on requirement
- **`submit()`**: Validates CVV if required

## Notes

- CVV visibility is determined **per card** when it's selected
- The logic is centralized in `isCvvRequired()` for easy maintenance
- You can easily extend the rules by modifying `isCvvRequired()`
