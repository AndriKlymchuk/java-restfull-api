package com.example.restfullapi.user;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import static com.example.restfullapi.constant.Constant.EMAIL_REGEX;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Email(message = "Please provide a valid email address", regexp = EMAIL_REGEX)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^\\+?[0-9]+$", message = "Please provide a valid phone number")
    private String phone;
}
