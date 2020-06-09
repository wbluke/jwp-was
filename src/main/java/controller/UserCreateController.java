package controller;

import db.DataBase;
import http.request.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import model.User;

@Slf4j
public class UserCreateController extends AbstractController {

    @Override
    void doGet(HttpRequest httpRequest) {
        User user = User.builder()
                .userId(httpRequest.getQueryStringValue("userId"))
                .password(httpRequest.getQueryStringValue("password"))
                .name(httpRequest.getQueryStringValue("name"))
                .email(httpRequest.getQueryStringValue("email"))
                .build();

        log.debug("User Create : {}", user);
        DataBase.addUser(user);
    }

    @Override
    void doPost(HttpRequest httpRequest) {
        User user = User.builder()
                .userId(httpRequest.getBody("userId"))
                .password(httpRequest.getBody("password"))
                .name(httpRequest.getBody("name"))
                .email(httpRequest.getBody("email"))
                .build();

        log.debug("User Create : {}", user);
        DataBase.addUser(user);
    }
}