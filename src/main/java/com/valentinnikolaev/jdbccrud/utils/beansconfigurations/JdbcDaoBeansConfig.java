package com.valentinnikolaev.jdbccrud.utils.beansconfigurations;

import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import com.valentinnikolaev.jdbccrud.repository.jdbc.JdbcPostRepositoryImpl;
import com.valentinnikolaev.jdbccrud.repository.jdbc.JdbcRegionRepositoryImpl;
import com.valentinnikolaev.jdbccrud.repository.jdbc.JdbcUserRepositoryImpl;
import com.valentinnikolaev.jdbccrud.utils.ConnectionFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan (basePackages = "com.valentinnikolaev.jdbccrud.repository")
public class JdbcDaoBeansConfig {

    @Bean
    @Scope("singleton")
    public UserRepository userRepository() {
        return new JdbcUserRepositoryImpl( postRepository());
    }

    @Bean
    @Scope("singleton")
    public PostRepository postRepository() {
        return new JdbcPostRepositoryImpl();
    }

    @Bean
    @Scope("singleton")
    public RegionRepository regionRepository() {
        return new JdbcRegionRepositoryImpl();
    }
}
