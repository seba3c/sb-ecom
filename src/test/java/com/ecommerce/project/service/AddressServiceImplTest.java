package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.dto.AddressResponse;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.util.AuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void getAllAddresses_returnsAddressResponse() {
        User user = new User();
        user.setId(1L);
        Address address1 = new Address();
        address1.setId(1L);
        address1.setStreetLine1("123 Main St");
        address1.setCity("New York");
        address1.setState("NY");
        address1.setCountry("USA");
        address1.setZipCode("10001");
        Address address2 = new Address();
        address2.setId(2L);
        address2.setStreetLine1("456 Oak Ave");
        address2.setCity("Los Angeles");
        address2.setState("CA");
        address2.setCountry("USA");
        address2.setZipCode("90001");
        AddressDTO dto1 = new AddressDTO(1L, "123 Main St", null, "New York", "NY", "USA", "10001");
        AddressDTO dto2 = new AddressDTO(2L, "456 Oak Ave", null, "Los Angeles", "CA", "USA", "90001");

        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByUser(user)).thenReturn(List.of(address1, address2));
        when(modelMapper.map(address1, AddressDTO.class)).thenReturn(dto1);
        when(modelMapper.map(address2, AddressDTO.class)).thenReturn(dto2);

        AddressResponse result = addressService.getAllAddresses();

        assertEquals(2, result.getContent().size());
        assertEquals("123 Main St", result.getContent().get(0).getStreetLine1());
    }

    @Test
    void getAllAddresses_emptyList_returnsEmptyContent() {
        User user = new User();
        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByUser(user)).thenReturn(List.of());

        AddressResponse result = addressService.getAllAddresses();

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAddressById_success() {
        User user = new User();
        user.setId(1L);
        Address address = new Address();
        address.setId(1L);
        address.setStreetLine1("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setZipCode("10001");
        AddressDTO dto = new AddressDTO(1L, "123 Main St", null, "New York", "NY", "USA", "10001");

        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(dto);

        AddressDTO result = addressService.getAddressById(1L);

        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getStreetLine1());
    }

    @Test
    void getAddressById_notFound_throwsResourceNotFoundException() {
        User user = new User();
        user.setId(1L);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddressById(99L));
    }

    @Test
    void createAddress_success() {
        User user = new User();
        user.setId(1L);
        AddressDTO inputDTO = new AddressDTO(null, "123 Main St", null, "New York", "NY", "USA", "10001");
        Address mappedAddress = new Address();
        mappedAddress.setStreetLine1("123 Main St");
        mappedAddress.setCity("New York");
        mappedAddress.setState("NY");
        mappedAddress.setCountry("USA");
        mappedAddress.setZipCode("10001");
        Address savedAddress = new Address();
        savedAddress.setId(1L);
        savedAddress.setStreetLine1("123 Main St");
        savedAddress.setCity("New York");
        savedAddress.setState("NY");
        savedAddress.setCountry("USA");
        savedAddress.setZipCode("10001");
        AddressDTO resultDTO = new AddressDTO(1L, "123 Main St", null, "New York", "NY", "USA", "10001");

        when(authUtils.loggedInUser()).thenReturn(user);
        when(modelMapper.map(inputDTO, Address.class)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(savedAddress);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(resultDTO);

        AddressDTO result = addressService.createAddress(inputDTO);

        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getStreetLine1());
        verify(addressRepository).save(mappedAddress);
    }

    @Test
    void updateAddress_success() {
        User user = new User();
        user.setId(1L);
        AddressDTO inputDTO = new AddressDTO(null, "Updated St", null, "Boston", "MA", "USA", "02101");
        Address existingAddress = new Address();
        existingAddress.setId(1L);
        existingAddress.setStreetLine1("123 Main St");
        Address mappedAddress = new Address();
        mappedAddress.setId(1L);
        mappedAddress.setStreetLine1("Updated St");
        mappedAddress.setCity("Boston");
        mappedAddress.setState("MA");
        mappedAddress.setCountry("USA");
        mappedAddress.setZipCode("02101");
        Address savedAddress = new Address();
        savedAddress.setId(1L);
        savedAddress.setStreetLine1("Updated St");
        savedAddress.setCity("Boston");
        savedAddress.setState("MA");
        savedAddress.setCountry("USA");
        savedAddress.setZipCode("02101");
        AddressDTO resultDTO = new AddressDTO(1L, "Updated St", null, "Boston", "MA", "USA", "02101");

        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingAddress));
        when(modelMapper.map(inputDTO, Address.class)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(savedAddress);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(resultDTO);

        AddressDTO result = addressService.updateAddress(1L, inputDTO);

        assertEquals(1L, result.getId());
        assertEquals("Updated St", result.getStreetLine1());
        verify(addressRepository).save(mappedAddress);
    }

    @Test
    void updateAddress_notFound_throwsResourceNotFoundException() {
        User user = new User();
        user.setId(1L);
        AddressDTO inputDTO = new AddressDTO(null, "Updated St", null, "Boston", "MA", "USA", "02101");
        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.updateAddress(99L, inputDTO));
    }

    @Test
    void deleteAddress_success() {
        User user = new User();
        user.setId(1L);
        Address address = new Address();
        address.setId(1L);
        address.setStreetLine1("123 Main St");

        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L);

        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAddress_notFound_throwsResourceNotFoundException() {
        User user = new User();
        user.setId(1L);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(addressRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.deleteAddress(99L));
    }
}