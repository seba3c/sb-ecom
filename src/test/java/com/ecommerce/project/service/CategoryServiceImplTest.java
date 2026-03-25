package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAllCategories_returnsCategoryResponse() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Electronics");
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Clothing");

        CategoryDTO dto1 = new CategoryDTO(1L, "Electronics");
        CategoryDTO dto2 = new CategoryDTO(2L, "Clothing");

        Page<Category> page = new PageImpl<>(List.of(cat1, cat2));
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(cat1, CategoryDTO.class)).thenReturn(dto1);
        when(modelMapper.map(cat2, CategoryDTO.class)).thenReturn(dto2);

        CategoryResponse response = categoryService.getAllCategories(0, 50, "id", "asc");

        assertEquals(2, response.getContent().size());
        assertEquals("Electronics", response.getContent().get(0).getName());
        assertEquals("Clothing", response.getContent().get(1).getName());
    }

    @Test
    void getAllCategories_emptyList_returnsEmptyContent() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        CategoryResponse response = categoryService.getAllCategories(0, 50, "id", "asc");

        assertTrue(response.getContent().isEmpty());
    }

    @Test
    void createCategory_success() {
        CategoryDTO inputDTO = new CategoryDTO(null, "Electronics");
        Category mappedCategory = new Category();
        mappedCategory.setName("Electronics");
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        CategoryDTO resultDTO = new CategoryDTO(1L, "Electronics");

        when(modelMapper.map(inputDTO, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.findByName("Electronics")).thenReturn(null);
        when(categoryRepository.save(mappedCategory)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDTO.class)).thenReturn(resultDTO);

        CategoryDTO result = categoryService.createCategory(inputDTO);

        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(mappedCategory);
    }

    @Test
    void createCategory_duplicateName_throwsAPIException() {
        CategoryDTO inputDTO = new CategoryDTO(null, "Electronics");
        Category mappedCategory = new Category();
        mappedCategory.setName("Electronics");
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        when(modelMapper.map(inputDTO, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.findByName("Electronics")).thenReturn(existingCategory);

        APIException ex = assertThrows(APIException.class,
                () -> categoryService.createCategory(inputDTO));

        assertTrue(ex.getMessage().contains("Electronics"));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        CategoryDTO dto = new CategoryDTO(1L, "Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(dto);

        CategoryDTO result = categoryService.deleteCategory(1L);

        assertEquals(1L, result.getId());
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_notFound_throwsResourceNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(99L));

        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void updateCategory_success() {
        CategoryDTO inputDTO = new CategoryDTO(null, "Updated Name");
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");
        Category mappedCategory = new Category();
        mappedCategory.setName("Updated Name");
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Updated Name");
        CategoryDTO resultDTO = new CategoryDTO(1L, "Updated Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(modelMapper.map(inputDTO, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.save(mappedCategory)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDTO.class)).thenReturn(resultDTO);

        CategoryDTO result = categoryService.updateCategory(1L, inputDTO);

        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(1L, mappedCategory.getId());
        verify(categoryRepository).save(mappedCategory);
    }

    @Test
    void updateCategory_notFound_throwsResourceNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, new CategoryDTO(null, "Name")));

        verify(categoryRepository, never()).save(any());
    }
}
