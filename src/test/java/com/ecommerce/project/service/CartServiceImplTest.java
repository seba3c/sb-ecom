package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setDiscount(BigDecimal.valueOf(0.00));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCartItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(product.getPrice());
        cartItem.setDiscount(product.getDiscount());

        productDTO = new ProductDTO(1L, "Laptop Pro", "desc", 10, BigDecimal.valueOf(999.99), BigDecimal.ZERO, null);
    }

    // --- addProductToCart ---

    @Test
    void addProductToCart_success_createsItemAndReturnsCart() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartDTO result = cartService.addProductToCart(1L, 2);

        assertNotNull(result);
        assertEquals(1, cart.getCartItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void addProductToCart_noExistingCart_createsNewCart() {
        Cart savedCart = new Cart();
        savedCart.setId(2L);
        savedCart.setUser(user);
        savedCart.setCartItems(new ArrayList<>());
        savedCart.setTotalPrice(BigDecimal.ZERO);

        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(savedCart, product)).thenReturn(Optional.empty());
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartDTO result = cartService.addProductToCart(1L, 1);

        assertNotNull(result);
    }

    @Test
    void addProductToCart_productNotFound_throwsResourceNotFoundException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(99L, 1));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_insufficientStock_throwsAPIException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(APIException.class, () -> cartService.addProductToCart(1L, 100));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_productAlreadyInCart_throwsAPIException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        assertThrows(APIException.class, () -> cartService.addProductToCart(1L, 2));
        verify(cartRepository, never()).save(any());
    }

    // --- updateProductQuantity ---

    @Test
    void updateProductQuantity_success_updatesQuantity() {
        cart.getCartItems().add(cartItem);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartDTO result = cartService.updateProductQuantity(1L, 5);

        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void updateProductQuantity_quantityZero_removesItem() {
        cart.getCartItems().add(cartItem);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.updateProductQuantity(1L, 0);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void updateProductQuantity_cartNotFound_throwsAPIException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> cartService.updateProductQuantity(1L, 3));
    }

    @Test
    void updateProductQuantity_itemNotInCart_throwsAPIException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> cartService.updateProductQuantity(1L, 3));
    }

    // --- removeProductFromCart ---

    @Test
    void removeProductFromCart_success_removesItem() {
        cart.getCartItems().add(cartItem);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.removeProductFromCart(1L);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeProductFromCart_itemNotInCart_throwsAPIException() {
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> cartService.removeProductFromCart(1L));
    }

    // --- getCart ---

    @Test
    void getCart_existingCart_returnsCartDTO() {
        cart.getCartItems().add(cartItem);
        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartDTO result = cartService.getCart();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getCartItems().size());
    }

    @Test
    void getCart_noExistingCart_createsAndReturnsEmptyCart() {
        Cart newCart = new Cart();
        newCart.setId(2L);
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        newCart.setTotalPrice(BigDecimal.ZERO);

        when(authUtils.loggedInUser()).thenReturn(user);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        CartDTO result = cartService.getCart();

        assertNotNull(result);
        assertTrue(result.getCartItems().isEmpty());
    }

    // --- getAllCarts ---

    @Test
    void getAllCarts_returnsAllCarts() {
        cart.getCartItems().add(cartItem);
        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<CartDTO> results = cartService.getAllCarts();

        assertEquals(1, results.size());
    }

    @Test
    void getAllCarts_noCarts_returnsEmptyList() {
        when(cartRepository.findAll()).thenReturn(List.of());

        List<CartDTO> results = cartService.getAllCarts();

        assertTrue(results.isEmpty());
    }
}
