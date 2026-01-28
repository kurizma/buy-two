// In your Angular cart service
calculateSubtotal(priceInclVat: number, vatRate: number = 0.20): number {
  return priceInclVat / (1 + vatRate);
}

// Usage for buy-02 cart
const subtotal = calculateSubtotal(45, 0.20); // 37.50


/////// Cart Display

Item: 45.00€ (incl. VAT)
Subtotal: 37.50€
VAT 20%: 7.50€
---------
Total: 45.00€