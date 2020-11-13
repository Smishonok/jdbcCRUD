package com.valentinNikolaev.jdbcCrud;

import com.valentinNikolaev.jdbcCrud.utils.beansConfigurations.ViewBeansConfig;
import com.valentinNikolaev.jdbcCrud.view.MainView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ViewBeansConfig.class);
        try {
            MainView mainView = context.getBean(MainView.class);
            mainView.initiateMainView();
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            e.getMessage();
        }
    }
}
