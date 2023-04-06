package com.ckdv.server.user.service;

import com.ckdv.server.user.request.RegisterRequest;
import com.ckdv.server.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User addUser(RegisterRequest user);
}
