package com.ckdv.server.user.service;

import com.ckdv.server.user.request.RegisterRequest;
import com.ckdv.server.user.model.User;
import com.ckdv.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository usersRepository;

    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    public User addUser(RegisterRequest user) {
        User newUser = new User();

        List<User> list = this.getAllUsers();

        newUser.setId((long) (list.size() + 1));
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        newUser.setRole("ROLE_USER");

        return usersRepository.save(newUser);
    }
}
