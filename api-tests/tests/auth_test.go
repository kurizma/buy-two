package tests

import (
	"net/http"
	"testing"

	"grit-tee/api-tests/client"
	"grit-tee/api-tests/config"
)

func TestClientProfile(t *testing.T) {
	resp := client.DoRequest(t, client.UserService, config.Client, "GET", "/api/users/me", nil)
	client.AssertOK(t, resp, http.StatusOK)
}

func TestSellerProfile(t *testing.T) {
	resp := client.DoRequest(t, client.UserService, config.Seller, "GET", "/api/users/me", nil)
	client.AssertOK(t, resp, http.StatusOK)
}
