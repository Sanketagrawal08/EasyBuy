package in.divyansh.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import in.divyansh.entity.Cart;
import in.divyansh.entity.Product;
import in.divyansh.entity.User;
import in.divyansh.repository.ProductRepository;
import in.divyansh.repository.UserRepository;


@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            User user1 = new User();
            user1.setFullName("Amit Patel");
            user1.setEmail("amit.patel1@gmail.com");
            user1.setPassword("amit123");
            user1.setPhone("9876543210");
            user1.setAddress("Ahmedabad, Gujarat");

            Cart cart1 = new Cart();
            cart1.setUser(user1);
            user1.setCart(cart1);

            userRepository.save(user1);


            User user2 = new User();
            user2.setFullName("Neha Sharma");
            user2.setEmail("neha.sharma@gmail.com");
            user2.setPassword("neha123");
            user2.setPhone("9876543211");
            user2.setAddress("Delhi, India");

            Cart cart2 = new Cart();
            cart2.setUser(user2);
            user2.setCart(cart2);

            userRepository.save(user2);
        }


        if (productRepository.count() == 0) {

            Product p1 = new Product();
            p1.setName("iPhone 15 Pro");
            p1.setDescription("Latest Apple smartphone");
            p1.setPrice(134900.00);
            p1.setStockQuantity(50);
            p1.setCategory("Electronics");
            p1.setBrand("Apple");
            p1.setImageUrl("https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRAkSJGJTNK_206jhAQChZFWURM7Y1a2YHEh7PoVbuLTwYJh0ujeNhaj78A_cZsE");
            p1.setSku("IPHONE15PRO");

            productRepository.save(p1);


            Product p2 = new Product();
            p2.setName("Samsung Galaxy S24");
            p2.setDescription("Samsung flagship smartphone");
            p2.setPrice(129999.00);
            p2.setStockQuantity(40);
            p2.setCategory("Electronics");
            p2.setBrand("Samsung");
            p2.setImageUrl("https://m.media-amazon.com/images/I/21nGAFj+EmL.jpg");
            p2.setSku("SAMSUNG-S24");

            productRepository.save(p2);


            Product p3 = new Product();
            p3.setName("Sony Headphones");
            p3.setDescription("Noise cancelling headphones");
            p3.setPrice(29990.00);
            p3.setStockQuantity(100);
            p3.setCategory("Electronics");
            p3.setBrand("Sony");
            p3.setImageUrl("https://m.media-amazon.com/images/I/31XKUEX6jgl._SY300_SX300_QL70_FMwebp_.jpg");
            p3.setSku("SONY-HEADPHONE");

            productRepository.save(p3);
        }
    }
}