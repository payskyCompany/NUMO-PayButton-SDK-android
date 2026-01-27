package io.paysky.paybutton.data.mock

import io.paysky.paybutton.data.model.response.CardItem
import io.paysky.paybutton.data.model.response.GetSessionResponse
import io.paysky.paybutton.data.model.response.ListSavedCardsResponse
import io.paysky.paybutton.data.model.response.UpdateCardsResponse
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Mock data repository for testing saved cards functionality without API calls.
 * This simulates the full cycle: get session, list cards, update default, delete cards.
 */
object MockDataRepository {
    
    // In-memory storage for mock cards
    private val mockCards: MutableList<CardItem> = CopyOnWriteArrayList()
    
    // Session ID for mock session
    private const val MOCK_SESSION_ID = "mock_session_12345"
    
    init {
        // Initialize with sample cards
        resetMockData()
    }
    
    /**
     * Reset mock data to initial state
     */
    @JvmStatic
    fun resetMockData() {
        mockCards.clear()
        mockCards.addAll(createDefaultMockCards())
    }
    
    /**
     * Create default mock cards for testing
     */
    private fun createDefaultMockCards(): List<CardItem> {
        return listOf(
            CardItem(
                brand = "Visa",
                cardId = 1,
                displayName = "Visa Card",
                isDefaultCard = true,
                maskedCardNumber = "**** **** **** 1234",
                postfix = "1234",
                token = "token_visa_1234",
                isSelected = false,
                isError = false,
                cvv = null
            ),
            CardItem(
                brand = "Mastercard",
                cardId = 2,
                displayName = "Mastercard",
                isDefaultCard = false,
                maskedCardNumber = "**** **** **** 5678",
                postfix = "5678",
                token = "token_mastercard_5678",
                isSelected = false,
                isError = false,
                cvv = null
            ),
            CardItem(
                brand = "Visa",
                cardId = 3,
                displayName = "Visa Corporate",
                isDefaultCard = false,
                maskedCardNumber = "**** **** **** 9012",
                postfix = "9012",
                token = "token_visa_9012",
                isSelected = false,
                isError = false,
                cvv = null
            ),
            CardItem(
                brand = "American Express",
                cardId = 4,
                displayName = "Amex Card (19 digits)",
                isDefaultCard = false,
                // Format: 19 digits total (4+4+4+4+3 visible digits pattern)
                maskedCardNumber = "1234 5678 9012 3456 789",
                postfix = "789",
                token = "token_amex_3456",
                isSelected = false,
                isError = false,
                cvv = null
            )
        )
    }
    
    /**
     * Mock GetSession response
     */
    @JvmStatic
    fun mockGetSession(): GetSessionResponse {
        return GetSessionResponse(
            message = "Session created successfully",
            referenceId = "ref_${System.currentTimeMillis()}",
            secureHash = "mock_secure_hash",
            secureHashData = "mock_secure_hash_data",
            statusCode = 200,
            success = true,
            transactionId = "txn_${System.currentTimeMillis()}",
            sessionId = MOCK_SESSION_ID
        )
    }
    
    /**
     * Mock ListSavedCards response
     */
    @JvmStatic
    fun mockListSavedCards(): ListSavedCardsResponse {
        return ListSavedCardsResponse(
            message = "Cards retrieved successfully",
            referenceId = "ref_${System.currentTimeMillis()}",
            secureHash = "mock_secure_hash",
            secureHashData = "mock_secure_hash_data",
            statusCode = 200,
            success = true,
            transactionId = "txn_${System.currentTimeMillis()}",
            cardsLists = ArrayList(mockCards) // Return a copy
        )
    }
    
    /**
     * Mock ChangeDefaultToken response
     */
    @JvmStatic
    fun mockChangeDefaultToken(token: String): UpdateCardsResponse {
        // Find the card and set it as default
        val cardIndex = mockCards.indexOfFirst { it.token == token }
        if (cardIndex != -1) {
            // Update all cards: remove default from all, set selected as default
            val updatedCards = mockCards.mapIndexed { index, card ->
                if (index == cardIndex) {
                    card.copy(isDefaultCard = true)
                } else {
                    card.copy(isDefaultCard = false)
                }
            }
            mockCards.clear()
            mockCards.addAll(updatedCards)
            
            return UpdateCardsResponse(
                message = "Default card changed successfully",
                referenceId = "ref_${System.currentTimeMillis()}",
                secureHash = "mock_secure_hash",
                secureHashData = "mock_secure_hash_data",
                statusCode = 200,
                success = true,
                transactionId = "txn_${System.currentTimeMillis()}",
                errorDetail = null,
                ErrorDescription = null
            )
        }
        
        // Card not found
        return UpdateCardsResponse(
            message = "Card not found",
            referenceId = "ref_${System.currentTimeMillis()}",
            secureHash = null,
            secureHashData = null,
            statusCode = 404,
            success = false,
            transactionId = null,
            errorDetail = "Card with token $token not found",
            ErrorDescription = "Card not found"
        )
    }
    
    /**
     * Mock DeleteTokenizedCard response
     */
    @JvmStatic
    fun mockDeleteTokenizedCard(token: String): UpdateCardsResponse {
        val cardIndex = mockCards.indexOfFirst { it.token == token }
        if (cardIndex != -1) {
            val wasDefault = mockCards[cardIndex].isDefaultCard
            mockCards.removeAt(cardIndex)
            
            // If deleted card was default and there are other cards, set first one as default
            if (wasDefault && mockCards.isNotEmpty()) {
                val firstCard = mockCards[0]
                mockCards[0] = firstCard.copy(isDefaultCard = true)
            }
            
            return UpdateCardsResponse(
                message = "Card deleted successfully",
                referenceId = "ref_${System.currentTimeMillis()}",
                secureHash = "mock_secure_hash",
                secureHashData = "mock_secure_hash_data",
                statusCode = 200,
                success = true,
                transactionId = "txn_${System.currentTimeMillis()}",
                errorDetail = null,
                ErrorDescription = null
            )
        }
        
        // Card not found
        return UpdateCardsResponse(
            message = "Card not found",
            referenceId = "ref_${System.currentTimeMillis()}",
            secureHash = null,
            secureHashData = null,
            statusCode = 404,
            success = false,
            transactionId = null,
            errorDetail = "Card with token $token not found",
            ErrorDescription = "Card not found"
        )
    }
    
    /**
     * Get current mock cards (for debugging)
     */
    fun getCurrentMockCards(): List<CardItem> {
        return ArrayList(mockCards)
    }
    
    /**
     * Add a new mock card (for testing)
     */
    fun addMockCard(card: CardItem) {
        mockCards.add(card)
    }
    
    /**
     * Clear all cards (for testing)
     */
    fun clearAllCards() {
        mockCards.clear()
    }
}
