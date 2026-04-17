package com.ecommerce.project.controller;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.service.UserDetailsServiceImpl;
import com.ecommerce.project.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCategories_returns200WithCategories() throws Exception {
        CategoryResponse response = new CategoryResponse(List.of(
                new CategoryDTO(1L, "Electronics"),
                new CategoryDTO(2L, "Clothing")
        ), 0, 50, 2L, 1, true);
        when(categoryService.getAllCategories(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Electronics"))
                .andExpect(jsonPath("$.content[1].name").value("Clothing"));
    }

    @Test
    void getAllCategories_empty_returns200() throws Exception {
        when(categoryService.getAllCategories(any(), any(), any(), any())).thenReturn(new CategoryResponse(List.of(), 0, 50, 0L, 0, true));

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void createCategory_returns201() throws Exception {
        CategoryDTO resultDTO = new CategoryDTO(1L, "Electronics");
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(resultDTO);

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDTO(null, "Electronics"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void createCategory_duplicate_returns400() throws Exception {
        when(categoryService.createCategory(any(CategoryDTO.class)))
                .thenThrow(new APIException("Category with the name Electronics already exists"));

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDTO(null, "Electronics"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category with the name Electronics already exists"));
    }

    @Test
    void deleteCategory_returns200() throws Exception {
        CategoryDTO dto = new CategoryDTO(1L, "Electronics");
        when(categoryService.deleteCategory(1L)).thenReturn(dto);

        mockMvc.perform(delete("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void deleteCategory_notFound_returns404() throws Exception {
        when(categoryService.deleteCategory(99L))
                .thenThrow(new ResourceNotFoundException("Category", "id", 99L));

        mockMvc.perform(delete("/api/admin/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id: 99 not found"));
    }

    @Test
    void updateCategory_returns200() throws Exception {
        CategoryDTO resultDTO = new CategoryDTO(1L, "Updated");
        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(resultDTO);

        mockMvc.perform(put("/api/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDTO(null, "Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void updateCategory_notFound_returns404() throws Exception {
        when(categoryService.updateCategory(eq(99L), any(CategoryDTO.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", 99L));

        mockMvc.perform(put("/api/admin/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDTO(null, "Updated"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id: 99 not found"));
    }
}
