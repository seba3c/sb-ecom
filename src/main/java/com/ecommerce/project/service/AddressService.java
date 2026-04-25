package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDetailResponse;
import com.ecommerce.project.dto.AddressListResponse;
import com.ecommerce.project.dto.CreateAddressRequest;
import com.ecommerce.project.dto.UpdateAddressRequest;

public interface AddressService {

    AddressListResponse getAllAddresses();

    AddressDetailResponse getAddressById(Long id);

    AddressDetailResponse createAddress(CreateAddressRequest request);

    AddressDetailResponse updateAddress(Long id, UpdateAddressRequest request);

    void deleteAddress(Long id);
}
