package co.ucc.esyrent.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.ucc.esyrent.dto.response.UserResponse;
import co.ucc.esyrent.security.JwtAuthFilter;
import co.ucc.esyrent.security.SecurityConfig;
import co.ucc.esyrent.security.UserDetailsServiceImpl;
import co.ucc.esyrent.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            invocation.<FilterChain>getArgument(2).doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldAllowAnonymousUserRegistration() throws Exception {
        when(userService.createUser(any())).thenReturn(
                new UserResponse(1L, "Jose", "jose@test.com", "300",
                        co.ucc.esyrent.domain.enums.UserRole.TENANT)
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Jose",
                                  "email": "jose@test.com",
                                  "password": "secret123",
                                  "phone": "300",
                                  "role": "TENANT"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectNonAdminAccessToUserListing() throws Exception {
        mockMvc.perform(get("/users").with(user("tenant@test.com").roles("TENANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminAccessToUserListing() throws Exception {
        when(userService.getAllUsers()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/users").with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
