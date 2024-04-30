package com.example.restfullapi.user;

import com.example.restfullapi.exception.CustomAppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Value("${user.min.age}")
    private int minAge;

    public void clear() {
        users.clear();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User create(User user) {
        if (!isValidDate(user.getBirthDate())) {
            throw new CustomAppException("Invalid date. User must have at least " + minAge + " years");
        }
        if (users.stream()
                .anyMatch(e -> e.getEmail().equals(user.getEmail()))) {
            throw new CustomAppException("Invalid create user. User with email already exists");
        }

        users.add(user);
        return user;
    }

    @Override
    public User update(String email, Map<String, ?> data) {
        for (User user : users) {
            if (email.equals(user.getEmail())) {
                for (Map.Entry<String, ?> entry : data.entrySet()) {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();
                    Class<?> userClass = user.getClass();
                    Field field = getFieldWithIgnoreCase(userClass, fieldName);
                    setValue(user, field, value);
                }
                return user;
            }
        }
        throw new CustomAppException("Invalid update user. User with email not found");
    }

    @Override
    public User save(User user) {
        for (int i = 0; i < users.size(); i++) {
            User existingUser = users.get(i);
            if (existingUser.getEmail().equals(user.getEmail())) {
                users.set(i, user);
                return user;
            }
        }
        throw new CustomAppException("Invalid save user. User with email not found");
    }

    @Override
    public void deleteByEmail(String email) {
        for (User user : users) {
            if (email.equals(user.getEmail())) {
                users.remove(user);
                return;
            }
        }
        throw new CustomAppException("Invalid delete user. User with email not found");
    }

    private boolean isValidDate(LocalDate date) {
        return Period.between(date, LocalDate.now()).getYears() >= minAge;
    }

    private Field getFieldWithIgnoreCase(Class<?> clazz, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                .findFirst().orElseThrow(() -> new CustomAppException("Invalid field by fieldName"));
    }

    private <T> void setValue(T entity, Field field, Object value) {
        field.setAccessible(true);
        try {
            if (field.getType() == LocalDate.class && !isValidDate((LocalDate) value)) {
                throw new CustomAppException("Invalid date. Date must have at least " + minAge + " years");
            }
            field.set(entity, value);
        } catch (IllegalAccessException | IllegalArgumentException | ClassCastException | DateTimeException e) {
            throw new CustomAppException("Invalid set value");
        }
    }
}
