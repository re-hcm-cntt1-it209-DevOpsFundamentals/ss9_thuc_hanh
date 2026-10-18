package com.example.dockerspringlab.controller;

import com.example.dockerspringlab.dto.ProductRequest;
import com.example.dockerspringlab.entity.Product;
import com.example.dockerspringlab.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        savedProduct = productRepository.save(new Product(
                "Màn hình Dell UltraSharp",
                "Màn hình đồ họa 27 inch 4K IPS",
                12500000.0,
                10
        ));
    }

    @Test
    @DisplayName("GET /api/products - lấy danh sách sản phẩm thành công")
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("Màn hình Dell UltraSharp"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - lấy chi tiết sản phẩm thành công")
    void shouldGetProductById() throws Exception {
        mockMvc.perform(get("/api/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("Màn hình Dell UltraSharp"))
                .andExpect(jsonPath("$.price").value(12500000.0));
    }

    @Test
    @DisplayName("GET /api/products/{id} - trả về 404 khi không tìm thấy")
    void shouldReturn404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/products - tạo mới sản phẩm thành công trả về 201")
    void shouldCreateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest(
                "Webcam Logitech C920",
                "Full HD 1080p, mic chống ồn tích hợp",
                1890000.0,
                15
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Webcam Logitech C920"))
                .andExpect(jsonPath("$.price").value(1890000.0))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    @DisplayName("POST /api/products - trả về 400 khi dữ liệu đầu vào không hợp lệ")
    void shouldReturn400WhenValidationFails() throws Exception {
        ProductRequest invalidRequest = new ProductRequest(
                "", // Tên trống
                "Mô tả",
                -500.0, // Giá âm
                -2 // Số lượng âm
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.name").isNotEmpty())
                .andExpect(jsonPath("$.details.price").isNotEmpty())
                .andExpect(jsonPath("$.details.quantity").isNotEmpty());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - cập nhật sản phẩm thành công")
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductRequest updateRequest = new ProductRequest(
                "Màn hình Dell UltraSharp (Đã cập nhật)",
                "Phiên bản nâng cấp màu sắc",
                13000000.0,
                8
        );

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Màn hình Dell UltraSharp (Đã cập nhật)"))
                .andExpect(jsonPath("$.price").value(13000000.0))
                .andExpect(jsonPath("$.quantity").value(8));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - xóa sản phẩm thành công trả về 204")
    void shouldDeleteProductSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNotFound());
    }
}
