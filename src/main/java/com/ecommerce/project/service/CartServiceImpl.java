package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.CartItemDTO;
import com.ecommerce.project.dto.CartResponse;
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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = fetchOrCreateCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getQuantity() < quantity) {
            throw new APIException("Insufficient stock for product: " + product.getName());
        }

        if (cartItemRepository.findByCartAndProduct(cart, product).isPresent()) {
            throw new APIException("Product already in cart. Use PUT to update quantity.");
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        item.setDiscount(product.getDiscount());
        cart.getCartItems().add(item);

        recalculateTotalPrice(cart);
        return toCartDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {

        Cart cart = fetchCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new APIException("Product not found in cart"));

        if (product.getQuantity() < quantity) {
            throw new APIException("Insufficient stock for product: " + product.getName());
        }
        item.setQuantity(quantity);

        recalculateTotalPrice(cart);
        return toCartDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO removeProductFromCart(Long productId) {

        Cart cart = fetchCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new APIException("Product not found in cart"));

        cart.getCartItems().remove(item);
        recalculateTotalPrice(cart);
        return toCartDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO getCart() {
        Cart cart = fetchOrCreateCart();
        return toCartDTO(cart);
    }

    @Override
    public CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return toCartResponse(cartRepository.findAll(toPageable(pageNumber, pageSize, sortBy, sortOrder)));
    }

    private Pageable toPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private CartResponse toCartResponse(Page<Cart> page) {
        List<CartDTO> cartDTOs = page.getContent().stream().map(this::toCartDTO).toList();
        return new CartResponse(cartDTOs, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    private void recalculateTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(i -> i.getPrice().subtract(i.getDiscount())
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private CartDTO toCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setCartItems(cart.getCartItems().stream()
                .map(item -> modelMapper.map(item, CartItemDTO.class))
                .toList());
        return cartDTO;
    }

    private Cart fetchOrCreateCart() {
        User user = authUtils.loggedInUser();
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private Cart fetchCart() {
        User user = authUtils.loggedInUser();
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new APIException("Cart not found"));
    }

}
