package com.example.restfullapi.user;

import com.example.restfullapi.exception.CustomAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository.clear();
    }

    @Test
    void findAll() {
        assertEquals(0, userRepository.findAll().size());
        User user = getNewUser();
        userRepository.create(user);
        user = getNewUser();
        user.setEmail("test_" + user.getEmail());
        userRepository.create(user);
        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    void createNewUserWithNoExistEmailSuccessful() {
        User user = getNewUser();
        User createUser = userRepository.create(user);
        assertEquals(user, createUser);
    }

    @Test
    void createNewUserWithExistEmailException() {
        User user = getNewUser();
        User createUser = userRepository.create(user);
        assertEquals(user, createUser);
        assertThrows(CustomAppException.class, () -> userRepository.create(user));
    }

    @Test
    void createNewUserWithNoValidDateException() {
        User user = getNewUser();
        user.setBirthDate(LocalDate.now());
        assertThrows(CustomAppException.class, () -> userRepository.create(user));
    }

    @Test
    void updateExistUserSuccessful() {
        User user = getNewUser();
        userRepository.create(user);

        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "Jane");
        User updateUser = userRepository.update(user.getEmail(), data);
        assertEquals("Jane", updateUser.getFirstName());
    }

    @Test
    void updateNoExistUserException() {
        User user = getNewUser();
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "Jane");
        assertThrows(CustomAppException.class, () -> userRepository.update(user.getEmail(), data));
    }

    @Test
    void updateExistUserWithNoValidDateException() {
        User user = getNewUser();
        userRepository.create(user);
        Map<String, Object> data = new HashMap<>();
        data.put("birthDate", LocalDate.now());
        assertThrows(CustomAppException.class, () -> userRepository.update(user.getEmail(), data));
        data.put("birthDate", "");
        assertThrows(CustomAppException.class, () -> userRepository.update(user.getEmail(), data));
    }

    @Test
    void saveNoExistUserException() {
        User user = getNewUser();
        assertThrows(CustomAppException.class, () -> userRepository.save(user));
    }

    @Test
    void saveExistUserSuccessful() {
        userRepository.create(getNewUser());
        User user = getNewUser();
        user.setFirstName("Update first name");
        user.setLastName("Update last name");
        User savedUser = userRepository.save(user);

        assertEquals(savedUser.getEmail(), user.getEmail());
        assertEquals(savedUser.getFirstName(), user.getFirstName());
        assertEquals(savedUser.getLastName(), user.getLastName());

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void deleteByEmailExistUserSuccessful() {
        User user = getNewUser();
        userRepository.create(user);
        assertEquals(1, userRepository.findAll().size());
        userRepository.deleteByEmail(getNewUser().getEmail());
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void deleteByEmailNoExistUserException() {
        assertThrows(CustomAppException.class, () -> userRepository.deleteByEmail(getNewUser().getEmail()));
    }

    private User getNewUser() {
        return new User(
                "test@test.com",
                "Jim",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Street",
                "123456789");
    }
}