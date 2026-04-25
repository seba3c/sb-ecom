package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AddressDetailResponse;
import com.ecommerce.project.dto.AddressListResponse;
import com.ecommerce.project.dto.CreateAddressRequest;
import com.ecommerce.project.dto.UpdateAddressRequest;
import com.ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<AddressListResponse> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDetailResponse> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @PostMapping
    public ResponseEntity<AddressDetailResponse> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDetailResponse> updateAddress(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
