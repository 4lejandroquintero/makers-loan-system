package com.makers.loans_backend.domain.port.in;

import com.makers.loans_backend.domain.model.User;
import java.util.List;

public interface UserUseCase {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
