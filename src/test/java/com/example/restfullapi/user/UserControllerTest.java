package com.example.restfullapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String API_PATH = "/api/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = getNewUser();
    }

    @Test
    void findWithValidPeriodSuccessful() throws Exception {
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH)
                        .param("from", user.getBirthDate().minusYears(1).toString())
                        .param("to", user.getBirthDate().plusYears(1).toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void findWithNoValidPeriodException() throws Exception {
        LocalDate today = LocalDate.now();
        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH)
                        .param("from", today.toString())
                        .param("to", today.toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createWithValidDataSuccessful() throws Exception {
        String content = objectMapper.writeValueAsString(user);

        when(userRepository.create(user)).thenReturn(user);

        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @Test
    void createWithNoValidDataException() throws Exception {
        User user = new User(
                "",
                "",
                "",
                LocalDate.now(),
                "",
                ""
        );
        String content = objectMapper.writeValueAsString(user);

        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void saveWithValidDataSuccessful() throws Exception {
        String content = objectMapper.writeValueAsString(user);

        when(userRepository.save(user)).thenReturn(user);

        mockMvc.perform(put(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @Test
    void updateWithValidDataSuccessful() throws Exception {
        user.setFirstName("aaa");
        user.setBirthDate(LocalDate.now().minusYears(19));

        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "aaa");
        data.put("birthDate", LocalDate.now().minusYears(19));

        when(userRepository.update(user.getEmail(), data)).thenReturn(user);

        mockMvc.perform(put(API_PATH + "/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    void updateWithNoValidDataException() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "");
        data.put("birthDate", LocalDate.now());

        mockMvc.perform(put(API_PATH + "/{email}", "no_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(API_PATH + "/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteWithValidDataSuccessful() throws Exception {
        mockMvc.perform(delete("/api/user/{email}", user.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWithNoValidDataException() throws Exception {
        mockMvc.perform(delete("/api/user/{email}", "no_email"))
                .andExpect(status().isBadRequest());
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