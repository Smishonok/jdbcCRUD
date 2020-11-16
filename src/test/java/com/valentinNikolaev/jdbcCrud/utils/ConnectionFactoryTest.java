package com.valentinNikolaev.jdbcCrud.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests of connection factory")
class ConnectionFactoryTest {

    private ConnectionFactory connectionFactory = new TestConnection().getConnectionFactory();

    @Test
    @DisplayName("When try to make transaction connection is exist")
    public void isConnectionExist() {
        //given
        Function<Connection, Boolean> transaction = connection->{
            boolean isValid = false;
            try {
                isValid = connection.isValid(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isValid;
        };

        //when
        boolean actualConnectionValidationStatus = connectionFactory.doTransaction(transaction);

        //then
        assertThat(actualConnectionValidationStatus).isEqualTo(true);
    }





}