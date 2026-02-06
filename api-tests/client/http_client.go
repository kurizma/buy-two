package client

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

type ServiceURL string

const (
	UserService    ServiceURL = "http://localhost:8456"
	ProductService ServiceURL = "http://localhost:8567"
	OrderService   ServiceURL = "http://localhost:8789"
	MediaService   ServiceURL = "http://localhost:8678"
)

func DoRequest(t *testing.T, service ServiceURL, user UserHeaders, method, path string, body any) *http.Response {
	// Fix: Clean path + join properly
	cleanPath := strings.TrimLeft(path, "/")
	url := string(service) + "/" + cleanPath // Ensures leading /

	var reqBody io.Reader
	if body != nil {
		jsonBody, err := json.Marshal(body)
		assert.NoError(t, err)
		reqBody = bytes.NewBuffer(jsonBody)
	}

	req, err := http.NewRequest(method, url, reqBody)
	assert.NoError(t, err)

	req.Header.Set("Authorization", "Bearer "+user.Token)
	req.Header.Set("X-USER-ID", user.ID)
	req.Header.Set("X-USER-ROLE", user.Role)
	req.Header.Set("Content-Type", "application/json")

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		t.Fatalf("Request failed %s %s: %v", method, url, err)
		return nil
	}
	return resp
}

type UserHeaders struct {
	ID    string
	Role  string
	Token string
}

func AssertOK(t *testing.T, resp *http.Response, expectedStatus int) {
	if resp == nil {
		t.Fatal("No response")
		return
	}
	defer resp.Body.Close()

	assert.Equal(t, expectedStatus, resp.StatusCode,
		fmt.Sprintf("Expected %d got %d", expectedStatus, resp.StatusCode))
}
