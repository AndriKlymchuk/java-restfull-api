package com.example.restfullapi.user;

import com.example.restfullapi.exception.CustomAppException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.example.restfullapi.constant.Constant.EMAIL_REGEX;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/user", produces = "application/json")
public class UserController {

    private final UserRepository repository;

    @GetMapping
    public List<User> find(
            @RequestParam("from") @Valid @Past LocalDate from,
            @RequestParam("to") @Valid @Past LocalDate to) {
        if (from.isAfter(to)) {
            throw new CustomAppException("Invalid date range");
        }

        return repository.findAll().stream()
                .filter(user -> user.getBirthDate().isAfter(from) &&
                        user.getBirthDate().isBefore(to))
                .toList();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return repository.create(user);
    }

    @PutMapping
    public User save(@Valid @RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{email}")
    public User update(
            @PathVariable
            @Valid
            @Email(message = "Please provide a valid email address", regexp = EMAIL_REGEX)
            String email,
            @Valid @RequestBody Map<String, ?> data) {

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString().isBlank()) {
                throw new CustomAppException("Invalid field " + entry.getKey() + " value. Value can't be null or blank");
            }
        }

        return repository.update(email, data);
    }

    @DeleteMapping("/{email}")
    public void delete(
            @PathVariable
            @Valid
            @Email(message = "Please provide a valid email address", regexp = EMAIL_REGEX)
            String email) {
        repository.deleteByEmail(email);
    }
}
