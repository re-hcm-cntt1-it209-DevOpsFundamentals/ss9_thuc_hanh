package com.example.dockerspringlab;

import com.example.dockerspringlab.entity.Product;
import com.example.dockerspringlab.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DockerSpringLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerSpringLabApplication.class, args);
    }

    @Bean
    public CommandLineRunner initSampleData(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(new Product(
                        "Bàn phím cơ DareU EK87",
                        "Bàn phím cơ tenkeyless, switch quang học, LED RGB phat moi sua lai lan 2",
                        650000.0,
                        25));
                productRepository.save(new Product(
                        "Chuột Logitech G102",
                        "Chuột chơi game cảm biến 8000 DPI, đèn LED Lightsync",
                        420000.0,
                        40));
                productRepository.save(new Product(
                        "Tai nghe Sony WH-CH520",
                        "Tai nghe bluetooth chụp tai, thời lượng pin 50 giờ",
                        1290000.0,
                        15));
            }
        };
    }
}
