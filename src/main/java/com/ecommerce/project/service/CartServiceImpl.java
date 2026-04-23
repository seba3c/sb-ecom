package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.CartItemDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
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
        User user = authUtils.loggedInUser();
        Cart cart = cartRepository.findByUserEmail(user.getEmail())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

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
        return toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {
        User user = authUtils.loggedInUser();
        Cart cart = cartRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new APIException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new APIException("Product not found in cart"));

        if (quantity == 0) {
            cart.getCartItems().remove(item);
        } else {
            if (product.getQuantity() < quantity) {
                throw new APIException("Insufficient stock for product: " + product.getName());
            }
            item.setQuantity(quantity);
        }

        recalculateTotalPrice(cart);
        return toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO removeProductFromCart(Long productId) {
        User user = authUtils.loggedInUser();
        Cart cart = cartRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new APIException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new APIException("Product not found in cart"));

        cart.getCartItems().remove(item);
        recalculateTotalPrice(cart);
        return toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO getCart() {
        User user = authUtils.loggedInUser();
        Cart cart = cartRepository.findByUserEmail(user.getEmail())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        return toDTO(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private void recalculateTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(i -> i.getPrice().subtract(i.getDiscount())
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private CartDTO toDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(item -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setId(item.getId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setDiscount(item.getDiscount());
                    dto.setProduct(modelMapper.map(item.getProduct(), ProductDTO.class));
                    return dto;
                })
                .toList();

        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setCartItems(itemDTOs);
        return cartDTO;
    }
}
