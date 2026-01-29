package tests

import (
	"net/http"
	"testing"

	"grit-tee/api-tests/client"
	"grit-tee/api-tests/config"
)

func TestClientCartToOrderPayOnDelivery(t *testing.T) {
	productId := "693d9398671f8d436cbe656c" // Flow State SPM Tee (has stock)

	// 1. Add to cart (CLIENT only)
	addBody := map[string]interface{}{
		"productId": productId,
		"quantity":  1,
	}
	respAdd := client.DoRequest(t, client.OrderService, config.Client, "POST", "/api/cart/items", addBody)
	client.AssertOK(t, respAdd, http.StatusOK)

	// 2. Verify cart populated
	respCart := client.DoRequest(t, client.OrderService, config.Client, "GET", "/api/cart", nil)
	client.AssertOK(t, respCart, http.StatusOK)

	// 3. Checkout Pay on Delivery
	respCheckout := client.DoRequest(t, client.OrderService, config.Client, "POST", "/api/orders/checkout", nil)
	client.AssertOK(t, respCheckout, http.StatusCreated)

	// 4. Verify order in buyer history
	respOrders := client.DoRequest(t, client.OrderService, config.Client, "GET", "/api/orders/buyer", nil)
	client.AssertOK(t, respOrders, http.StatusOK)
}
