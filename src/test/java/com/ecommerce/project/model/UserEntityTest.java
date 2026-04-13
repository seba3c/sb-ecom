package com.ecommerce.project.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserEntityTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void userPersistsRolesProductsAndAddresses() {
        Role role = new Role();
        role.setName(AppRole.Seller);

        User user = new User();
        user.setUsername("seller01");
        user.setEmail("seller01@example.com");
        user.setPassword("secret-password");
        user.getRoles().add(role);

        Product product = new Product();
        product.setName("Laptop Pro");
        product.setDescription("High performance laptop");
        product.setQuantity(10);
        product.setPrice(999.99);
        product.setDiscount(0.1);
        product.setSeller(user);
        user.getProducts().add(product);

        Address address = new Address();
        address.setStreetLine1("12345 Main St");
        address.setStreetLine2("Suite 100");
        address.setCity("Madrid");
        address.setState("Madrid");
        address.setCountry("Spain");
        address.setZipcode("28001");
        address.setUser(user);
        user.getAddresses().add(address);

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        User savedUser = entityManager.find(User.class, user.getId());

        assertNotNull(savedUser);
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(AppRole.Seller, savedUser.getRoles().iterator().next().getName());
        assertEquals(1, savedUser.getProducts().size());
        assertEquals(1, savedUser.getAddresses().size());

        Product savedProduct = savedUser.getProducts().iterator().next();
        assertEquals(savedUser.getId(), savedProduct.getSeller().getId());

        Address savedAddress = savedUser.getAddresses().iterator().next();
        assertEquals(savedUser.getId(), savedAddress.getUser().getId());
    }
}
