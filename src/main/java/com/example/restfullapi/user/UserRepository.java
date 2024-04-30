package com.example.restfullapi.user;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    List<User> findAll();
    User create(User user);
    User update(String email, Map<String,?> data);
    User save(User user);
    void deleteByEmail(String email);
}
