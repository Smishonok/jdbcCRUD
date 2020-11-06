package com.valentinNikolaev.jdbcCrud;

import com.valentinNikolaev.jdbcCrud.view.MainView;

public class Main {

    public static void main(String[] args) {
        try {
            MainView mainView = new MainView();
            mainView.initiateMainView();
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            e.getMessage();
        }
    }
}
