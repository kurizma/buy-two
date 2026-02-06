package config

// Import client to reuse type
import "grit-tee/api-tests/client"

var (
    Client = client.UserHeaders{
        ID:    "6978f3b045b43051c23f2701",
        Role:  "CLIENT",
        Token: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWNAY2hlZXNlLmtyIiwiaWQiOiI2OTc4ZjNiMDQ1YjQzMDUxYzIzZjI3MDEiLCJyb2xlIjoiQ0xJRU5UIiwiaWF0IjoxNzY5Njk2OTE4LCJleHAiOjE3Njk3ODMzMTh9.MZaUYEipySIrEZVPSyQhFEY_qtmgRFlnK2mVyvCS3-s",
    }
    Seller = client.UserHeaders{
        ID:    "693f98995940a77e1d19246a",
        Role:  "SELLER", 
        Token: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYWNraWVAY2hhbi5jbiIsImlkIjoiNjkzZjk4OTk1OTQwYTc3ZTFkMTkyNDZhIiwicm9sZSI6IlNFTExFUiIsImlhdCI6MTc2OTY5Njg0OSwiZXhwIjoxNzY5NzgzMjQ5fQ.JUPjineA__q9buxv_sEViAYe0ntgyqr-0y2da7WVG9k",
    }
)
