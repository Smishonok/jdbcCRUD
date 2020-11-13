package com.valentinNikolaev.jdbcCrud.utils.beansConfigurations;

import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import com.valentinNikolaev.jdbcCrud.repository.RegionRepository;
import com.valentinNikolaev.jdbcCrud.repository.UserRepository;
import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.PostRepositoryImpl;
import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.RegionRepositoryImpl;
import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.UserRepositoryImpl;
import com.valentinNikolaev.jdbcCrud.utils.ConnectionFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan (basePackages = "com.valentinNikolaev.jdbcCrud.repository")
public class JdbcDaoBeansConfig {

    @Bean
    @Description ("Create jdbc connection factory")
    @Scope ("singleton")
    public ConnectionFactory connectionFactory() {
        return new ConnectionFactory();
    }

    @Bean
    @Scope("singleton")
    public UserRepository userRepository() {
        return new UserRepositoryImpl(connectionFactory(), postRepository());
    }

    @Bean
    @Scope("singleton")
    public PostRepository postRepository() {
        return new PostRepositoryImpl(connectionFactory());
    }

    @Bean
    @Scope("singleton")
    public RegionRepository regionRepository() {
        return new RegionRepositoryImpl(connectionFactory());
    }
}
