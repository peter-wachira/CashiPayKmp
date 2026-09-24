Feature: Send and list payments
  As a CashiPay user
  I want payments to be validated, processed, and recorded
  So that I can trust the payment activity shown in the app

  Scenario: Valid payment is processed and saved
    Given the recipient email is "customer@example.com"
    And the amount is "25.00"
    And the currency is "USD"
    When the user reviews and confirms the payment
    Then the backend processes the payment
    And the completed payment is saved to the user's Firestore activity

  Scenario: Invalid recipient is rejected before processing
    Given the recipient email is "invalid-email"
    And the amount is "25.00"
    And the currency is "USD"
    When the user reviews the payment
    Then a recipient validation error is displayed
    And the backend is not called

  Scenario: Processed payment appears in real-time activity
    Given a completed payment has been saved for the authenticated user
    When Firestore publishes the updated payment snapshot
    Then the payment appears in transaction history
    And its recipient, amount, currency, and timestamp are displayed
