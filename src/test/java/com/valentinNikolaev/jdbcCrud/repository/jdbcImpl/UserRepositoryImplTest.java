package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.models.Role;
import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import com.valentinNikolaev.jdbcCrud.repository.RegionRepository;
import com.valentinNikolaev.jdbcCrud.repository.UserRepository;
import com.valentinNikolaev.jdbcCrud.utils.ConnectionFactory;
import com.valentinNikolaev.jdbcCrud.utils.TestConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@DisplayName ("Tests for user DAO class based on JDBC")
class UserRepositoryImplTest {

    private ConnectionFactory connectionFactory = new TestConnectionFactory().getConnectionFactory();
    private PostRepository postRepository = new PostRepositoryImpl(connectionFactory);
    private UserRepository userRepository = new UserRepositoryImpl(connectionFactory,
                                                                   postRepository);
    private RegionRepository regionRepository = new RegionRepositoryImpl(connectionFactory);

    @Nested
    @DisplayName ("Test of add method")
    class addMethodTests {

        @Test
        @DisplayName ("When user added into database, then database contains user")
        public void whenAddNewUserThenUserExistInDataBase() {
            //given
            Region region = new Region(1, "TestRegion");
            User expectedUser = new User(1, "UserName", "UserLastName", region, Role.ADMIN);

            //when
            regionRepository.add(region);
            userRepository.add(expectedUser);
            User actualUser = getUserFromDb(expectedUser.getFirstName(),
                                            expectedUser.getLastName());

            //then
            assertThat(actualUser)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "region")
                    .isEqualTo(expectedUser);

        }

        private User getUserFromDb(String firstName, String lastName) {
            Function<Connection, User> transaction = connection->{
                User user = null;
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "select * from users where first_name=? and last_name=?");
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        String userFirstName = resultSet.getString("first_name");
                        String userLastName = resultSet.getString("last_name");
                        Role userRole = Role.valueOf(resultSet.getString("role"));
                        user = new User(id, userFirstName, userLastName, null, userRole);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return user;
            };

            return connectionFactory.doTransaction(transaction);
        }


        private void addUser(User user) {
            userRepository.add(user);
        }

    }

    @Test
    @DisplayName ("After adding new user to the repository, repository should contains user")
    public void addMethodTest1() {

    }

    @Test
    @DisplayName ("When adding user,which exists in repository, the exception should be thrown.")
    public void addMethodTest2() {

    }

}