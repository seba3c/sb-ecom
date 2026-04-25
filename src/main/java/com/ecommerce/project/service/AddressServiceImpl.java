package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDetailResponse;
import com.ecommerce.project.dto.AddressListResponse;
import com.ecommerce.project.dto.AddressCreateRequest;
import com.ecommerce.project.dto.AddressUpdateRequest;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.util.AuthUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressListResponse getAllAddresses() {
        User user = authUtils.loggedInUser();
        List<Address> addresses = addressRepository.findByUser(user);
        List<AddressDetailResponse> responses = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDetailResponse.class))
                .toList();
        return new AddressListResponse(responses);
    }

    @Override
    public AddressDetailResponse getAddressById(Long id) {
        User user = authUtils.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        return modelMapper.map(address, AddressDetailResponse.class);
    }

    @Override
    public AddressDetailResponse createAddress(AddressCreateRequest request) {
        User user = authUtils.loggedInUser();
        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDetailResponse.class);
    }

    @Override
    public AddressDetailResponse updateAddress(Long id, AddressUpdateRequest request) {
        User user = authUtils.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        address.setStreetLine1(request.getStreetLine1());
        address.setStreetLine2(request.getStreetLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());

        return modelMapper.map(addressRepository.save(address), AddressDetailResponse.class);
    }

    @Override
    public void deleteAddress(Long id) {
        User user = authUtils.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        addressRepository.delete(address);
    }
}
