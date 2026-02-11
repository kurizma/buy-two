package com.buyone.orderservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTests {

    @Test
    void testAddressBuilder() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .phone("+12025551234")
                .build();

        assertEquals("123 Main St", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZipCode());
        assertEquals("USA", address.getCountry());
        assertEquals("+12025551234", address.getPhone());
    }

    @Test
    void testAddressNoArgsConstructor() {
        Address address = new Address();
        assertNull(address.getStreet());
        assertNull(address.getCity());
        assertNull(address.getState());
        assertNull(address.getZipCode());
        assertNull(address.getCountry());
        assertNull(address.getPhone());
    }

    @Test
    void testAddressAllArgsConstructor() {
        Address address = new Address("John Doe", "123 Main St", "New York", "NY", 
                "10001", "USA", "+12025551234");

        assertEquals("John Doe", address.getFullName());
        assertEquals("123 Main St", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZipCode());
        assertEquals("USA", address.getCountry());
        assertEquals("+12025551234", address.getPhone());
    }

    @Test
    void testAddressSetters() {
        Address address = new Address();
        address.setStreet("456 Oak Ave");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipCode("90001");
        address.setCountry("USA");
        address.setPhone("+13105551234");

        assertEquals("456 Oak Ave", address.getStreet());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("90001", address.getZipCode());
        assertEquals("USA", address.getCountry());
        assertEquals("+13105551234", address.getPhone());
    }

    @Test
    void testAddressEqualsAndHashCode() {
        Address address1 = Address.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .country("USA")
                .build();

        Address address2 = Address.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .country("USA")
                .build();

        Address address3 = Address.builder()
                .street("456 Oak Ave")
                .city("Los Angeles")
                .zipCode("90001")
                .country("USA")
                .build();

        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
        assertNotEquals(address1, address3);
    }

    @Test
    void testAddressToString() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .build();

        String toString = address.toString();
        assertTrue(toString.contains("123 Main St"));
        assertTrue(toString.contains("New York"));
        assertTrue(toString.contains("10001"));
    }

    @Test
    void testAddressWithOptionalState() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("London")
                .zipCode("12345")
                .country("UK")
                .build();

        assertNull(address.getState());
        assertNotNull(address.getStreet());
        assertNotNull(address.getCity());
    }

    @Test
    void testAddressWithOptionalPhone() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .country("USA")
                .build();

        assertNull(address.getPhone());
    }
}
