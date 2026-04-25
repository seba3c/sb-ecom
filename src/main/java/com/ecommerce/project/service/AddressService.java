package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.dto.AddressResponse;

public interface AddressService {

    AddressResponse getAllAddresses();

    AddressDTO getAddressById(Long id);

    AddressDTO createAddress(AddressDTO addressDTO);

    AddressDTO updateAddress(Long id, AddressDTO addressDTO);

    void deleteAddress(Long id);
}