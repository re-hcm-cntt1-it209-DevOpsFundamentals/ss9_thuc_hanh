package com.example.dockerspringlab.service;

import com.example.dockerspringlab.dto.ProductRequest;
import com.example.dockerspringlab.dto.ProductResponse;
import com.example.dockerspringlab.entity.Product;
import com.example.dockerspringlab.exception.ResourceNotFoundException;
import com.example.dockerspringlab.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Laptop Lenovo ThinkPad", "Core i7, 16GB RAM", 25000000.0, 5);
    }

    @Test
    @DisplayName("getAllProducts - trả về danh sách ProductResponse")
    void shouldReturnAllProducts() {
        Product product2 = new Product(2L, "MacBook Pro", "M3, 16GB RAM", 35000000.0, 3);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("Laptop Lenovo ThinkPad", responses.get(0).getName());
        assertEquals("MacBook Pro", responses.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getProductById - trả về chi tiết sản phẩm khi ID tồn tại")
    void shouldReturnProductWhenIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop Lenovo ThinkPad", response.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById - ném ra ResourceNotFoundException khi ID không tồn tại")
    void shouldThrowExceptionWhenIdDoesNotExist() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("createProduct - lưu và trả về ProductResponse")
    void shouldSaveAndReturnProduct() {
        ProductRequest request = new ProductRequest("Ổ cứng SSD 1TB", "NVMe PCIe 4.0", 1950000.0, 20);
        Product savedEntity = new Product(10L, request.getName(), request.getDescription(), request.getPrice(), request.getQuantity());

        when(productRepository.save(any(Product.class))).thenReturn(savedEntity);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Ổ cứng SSD 1TB", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - cập nhật và lưu thay đổi")
    void shouldUpdateProductWhenFound() {
        ProductRequest updateRequest = new ProductRequest("Laptop Lenovo ThinkPad X1", "Core i7, 32GB RAM", 28000000.0, 4);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Laptop Lenovo ThinkPad X1", response.getName());
        assertEquals(28000000.0, response.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("deleteProduct - xóa entity khi tìm thấy")
    void shouldDeleteProductWhenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("deleteProduct - ném ra ResourceNotFoundException khi không tìm thấy")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any());
    }
}
