package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDetailResponse;
import com.ecommerce.project.dto.AddressListResponse;
import com.ecommerce.project.dto.AddressCreateRequest;
import com.ecommerce.project.dto.AddressUpdateRequest;

public interface AddressService {

    AddressListResponse getAllAddresses();

    AddressDetailResponse getAddressById(Long id);

    AddressDetailResponse createAddress(AddressCreateRequest request);

    AddressDetailResponse updateAddress(Long id, AddressUpdateRequest request);

    void deleteAddress(Long id);
}
