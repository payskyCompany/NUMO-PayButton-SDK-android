# Mock Data for Saved Cards Testing

This mock data system allows you to test the saved cards functionality without making actual API calls.

## Features

- ✅ **Full Cycle Support**: Get session, list cards, change default card, delete cards
- ✅ **Realistic Behavior**: Simulates network delays (800ms)
- ✅ **State Management**: Maintains card state across operations
- ✅ **Easy Toggle**: Enable/disable with a single method call

## Usage

### Enable Mock Data Mode

In your `MainActivity` or `Application` class:

```java
// Enable mock data mode
ApiConnection.setUseMockData(true);

// Or disable to use real API
ApiConnection.setUseMockData(false);
```

### Example in MainActivity

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Initialize Chucker for network debugging
    ApiConnection.initializeChucker(this);
    
    // Enable mock data for testing
    ApiConnection.setUseMockData(true); // Set to false for real API
    
    setContentView(R.layout.activity_main);
    // ... rest of your code
}
```

## Mock Data Included

The mock repository includes 4 sample cards:

1. **Visa Card** (Default) - `**** **** **** 1234`
2. **Mastercard** - `**** **** **** 5678`
3. **Visa Corporate** - `**** **** **** 9012`
4. **Amex Card** - `**** ****** *3456`

## Supported Operations

### 1. Get Session
- Returns a mock session ID: `mock_session_12345`
- Simulates successful session creation

### 2. List Saved Cards
- Returns all mock cards
- First card is set as default

### 3. Change Default Card
- Updates the default card flag
- Removes default from previous card
- Returns success response

### 4. Delete Card
- Removes card from the list
- If deleted card was default, sets first remaining card as default
- Returns success response

## Reset Mock Data

To reset mock data to initial state:

```java
MockDataRepository.resetMockData();
```

## Debug Logging

When mock mode is enabled, you'll see debug logs prefixed with `🔧 [MOCK]`:
- `🔧 [MOCK] getSession - Returning mock session`
- `🔧 [MOCK] listSavedCards - Returning mock cards list`
- `🔧 [MOCK] changeDefaultToken - Token: token_xxx`
- `🔧 [MOCK] deleteTokenizedCard - Token: token_xxx`

## Testing Scenarios

### Test List Cards
1. Enable mock mode
2. Navigate to saved cards screen
3. You should see 4 cards displayed

### Test Change Default
1. Select a non-default card
2. Set it as default
3. Verify the default indicator moves to the selected card

### Test Delete Card
1. Delete a card
2. Verify it's removed from the list
3. If it was default, verify another card becomes default

### Test Empty State
```java
// Clear all cards to test empty state
MockDataRepository.clearAllCards();
// Then list cards - should return empty list
```

## Notes

- Mock data is stored in memory and resets when app restarts
- Network delay is simulated (800ms) for realistic testing
- All operations return success responses (except invalid token scenarios)
- Mock mode only affects saved cards operations, other API calls remain unchanged
