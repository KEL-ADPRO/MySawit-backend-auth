package com.mysawit.mysawit_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.request.RefreshRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.handler.GlobalExceptionHandler;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.AuthService;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import com.mysawit.mysawit_auth.util.CookieUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
        @Mock
        private AuthService authService;

        @Mock
        private AuthStrategyFactory strategyFactory;

        @Mock
        private AuthStrategy strategy;

        @Mock
        private CookieUtil cookieUtil;

        @InjectMocks
        private AuthController authController;

        private MockMvc mockMvc;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private AuthRequest validLoginRequest;
        private AuthResponse loginResponse;

        private RegisterRequest adminRequest;
        private RegisterRequest mandorRequest;
        private RegisterRequest buruhRequest;
        private RegisterRequest supirRequest;

        private AuthResponse adminResponse;
        private AuthResponse mandorResponse;
        private AuthResponse buruhResponse;
        private AuthResponse supirResponse;

        private static final String AUTH_COOKIE = "auth_token=dummy.jwt.token; Path=/; HttpOnly; SameSite=Strict";
        private static final String CLEAR_COOKIE = "auth_token=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Strict";

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(authController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                adminRequest = RegisterRequest.builder()
                                .username("Admin Sawit")
                                .name("Agus")
                                .email("admin@gmail.com")
                                .password("admin123")
                                .role(Role.ADMIN)
                                .build();

                mandorRequest = RegisterRequest.builder()
                                .username("Mandor Sawit")
                                .name("Burhan")
                                .email("burhan@gmail.com")
                                .password("mandor123")
                                .role(Role.MANDOR)
                                .nomorSertifMandor("CERT-001")
                                .build();

                buruhRequest = RegisterRequest.builder()
                                .username("Buruh Sawit")
                                .name("Usep")
                                .email("usep@gmail.com")
                                .password("buruh123")
                                .role(Role.BURUH)
                                .build();

                supirRequest = RegisterRequest.builder()
                                .username("Supir Sawit")
                                .name("Budi")
                                .email("budi@gmail.com")
                                .password("supir123")
                                .role(Role.SUPIR)
                                .build();

                adminResponse = AuthResponse.builder()
                                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                                .username("Admin Sawit")
                                .name("Agus")
                                .email("admin@gmail.com")
                                .role(Role.ADMIN)
                                .build();

                mandorResponse = AuthResponse.builder()
                                .userId(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"))
                                .username("Mandor Sawit")
                                .name("Burhan")
                                .email("burhan@gmail.com")
                                .role(Role.MANDOR)
                                .nomorSertifMandor("CERT-001")
                                .build();

                buruhResponse = AuthResponse.builder()
                                .userId(UUID.fromString("136d7ac3-2ee7-4e74-8225-e9062540a6c8"))
                                .username("Buruh Sawit")
                                .name("Usep")
                                .email("usep@gmail.com")
                                .role(Role.BURUH)
                                .build();

                supirResponse = AuthResponse.builder()
                                .userId(UUID.fromString("ec1d34bc-49a2-46d1-8cf0-ac8e559d508a"))
                                .username("Supir Sawit")
                                .name("Budi")
                                .email("budi@gmail.com")
                                .role(Role.SUPIR)
                                .build();

                loginResponse = AuthResponse.builder()
                                .token("dummy.jwt.token")
                                .refreshToken("dummy.refresh.token")
                                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                                .username("Admin Sawit")
                                .name("Agus")
                                .email("admin@gmail.com")
                                .role(Role.ADMIN)
                                .build();

                validLoginRequest = AuthRequest.builder()
                        .email("admin@gmail.com")
                        .password("Admin_12345")
                        .build();
        }

        @Test
        void registerAdminSuccess() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(adminResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adminRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration successful"))
                                .andExpect(jsonPath("$.data.username").value("Admin Sawit"))
                                .andExpect(jsonPath("$.data.role").value("ADMIN"));

                verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void registerMandorSuccess() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(mandorResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mandorRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration successful"))
                                .andExpect(jsonPath("$.data.username").value("Mandor Sawit"))
                                .andExpect(jsonPath("$.data.nomorSertifMandor").value("CERT-001"))
                                .andExpect(jsonPath("$.data.role").value("MANDOR"));

                verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void registerBuruhSuccess() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(buruhResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(buruhRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration successful"))
                                .andExpect(jsonPath("$.data.username").value("Buruh Sawit"))
                                .andExpect(jsonPath("$.data.role").value("BURUH"));

                verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void registerSupirSuccess() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(supirResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(supirRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration successful"))
                                .andExpect(jsonPath("$.data.username").value("Supir Sawit"))
                                .andExpect(jsonPath("$.data.role").value("SUPIR"));

                verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void loginSuccess() throws Exception {
                when(authService.login(any(AuthRequest.class))).thenReturn(loginResponse);

                String cookie = "auth_token=dummy.jwt.token; Path=/; HttpOnly; SameSite=Strict";
                when(cookieUtil.addAuthCookie("dummy.jwt.token")).thenReturn(cookie);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Login successful"))
                                .andExpect(jsonPath("$.data.token").value("dummy.jwt.token"))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE, cookie));

                verify(authService, times(1)).login(any(AuthRequest.class));
                verify(cookieUtil, times(1)).addAuthCookie("dummy.jwt.token");
        }

        @Test
        void registerInvalidCredentials() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenThrow(new InvalidCredentialException());

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adminRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }

        @Test
        void registerEmailAlreadyExists() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenThrow(new EmailAlreadyExistsException(adminRequest.getEmail()));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adminRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Email admin@gmail.com is already registered"));
        }

        @Test
        void registerMandorSertifMissing() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenThrow(new MandorSertifMissingException());

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adminRequest)))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message")
                                                .value("Nomor sertifikasi mandor is required for MANDOR role"));
        }

        @Test
        void loginInvalidCredentials() throws Exception {
                when(authService.login(any(AuthRequest.class))).thenThrow(new InvalidCredentialException());

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid credentials"));

                verify(authService, times(1)).login(any(AuthRequest.class));
        }

        @Test
        void logoutSuccess_viaHeader() throws Exception {
                doNothing().when(authService).logout("valid.jwt.token");
                when(cookieUtil.clearAuthCookie()).thenReturn(CLEAR_COOKIE);

                mockMvc.perform(post("/api/auth/logout")
                                .header("Authorization", "Bearer valid.jwt.token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Logout successful"))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE, CLEAR_COOKIE));

                verify(authService).logout("valid.jwt.token");
                verify(cookieUtil).clearAuthCookie();
        }

        @Test
        void logoutMalformedHeader() throws Exception {
                mockMvc.perform(post("/api/auth/logout")
                                .header("Authorization", "NotBearer token"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid Authorization header"));

                verify(authService, never()).logout(any());
        }

        @Test
        void logoutInvalidToken() throws Exception {
                doThrow(new InvalidCredentialException()).when(authService).logout("bad.token");

                mockMvc.perform(post("/api/auth/logout")
                                .header("Authorization", "Bearer bad.token"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value("Invalid credentials"));

                verify(cookieUtil, never()).clearAuthCookie();
        }

        @Test
        void getMeValidToken_viaHeader() throws Exception {
                when(authService.getLoggedInUser("valid.jwt.token")).thenReturn(adminResponse);

                mockMvc.perform(get("/api/auth/me")
                                .header("Authorization", "Bearer valid.jwt.token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User retrieved"))
                                .andExpect(jsonPath("$.data.email").value("admin@gmail.com"));

                verify(authService).getLoggedInUser("valid.jwt.token");
        }

        @Test
        void getMeValidToken_viaCookie() throws Exception {
                when(authService.getLoggedInUser("cookie.jwt.token")).thenReturn(adminResponse);

                mockMvc.perform(get("/api/auth/me")
                                .cookie(new jakarta.servlet.http.Cookie(CookieUtil.AUTH_COOKIE_NAME, "cookie.jwt.token")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User retrieved"))
                                .andExpect(jsonPath("$.data.email").value("admin@gmail.com"));

                verify(authService).getLoggedInUser("cookie.jwt.token");
        }

        @Test
        void getMeHeaderTakesPriorityOverCookie() throws Exception {
                when(authService.getLoggedInUser("header.jwt.token")).thenReturn(adminResponse);

                mockMvc.perform(get("/api/auth/me")
                                .header("Authorization", "Bearer header.jwt.token")
                                .cookie(new jakarta.servlet.http.Cookie(CookieUtil.AUTH_COOKIE_NAME, "cookie.jwt.token")))
                                .andExpect(status().isOk());

                verify(authService).getLoggedInUser("header.jwt.token");
                verify(authService, never()).getLoggedInUser("cookie.jwt.token");
        }

        @Test
        void getMeNoHeaderNoCookie() throws Exception {
                mockMvc.perform(get("/api/auth/me"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid Authorization header"));
        }

        @Test
        void getMeMalformedHeader_noCookie() throws Exception {
                mockMvc.perform(get("/api/auth/me")
                                .header("Authorization", "NotBearer token"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid Authorization header"));
        }

        @Test
        void getMeMalformedHeader_fallsBackToCookie() throws Exception {
                when(authService.getLoggedInUser("cookie.jwt.token")).thenReturn(adminResponse);

                mockMvc.perform(get("/api/auth/me")
                                .header("Authorization", "NotBearer token")
                                .cookie(new jakarta.servlet.http.Cookie(CookieUtil.AUTH_COOKIE_NAME,
                                                "cookie.jwt.token")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.email").value("admin@gmail.com"));

                verify(authService).getLoggedInUser("cookie.jwt.token");
        }

        @Test
        void refreshSuccessWithCookie() throws Exception {
                final String oldRefreshToken = "old.refresh.token";
                final String newAccessToken = "new.jwt.token";
                final String newRefreshToken = "new.refresh.token";

                final AuthResponse refreshedResponse = AuthResponse.builder()
                                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                                .username("Admin Sawit")
                                .role(Role.ADMIN)
                                .token(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();

                when(authService.refresh(oldRefreshToken)).thenReturn(refreshedResponse);

                final String accessCookie = "auth_token=" + newAccessToken + "; Path=/; HttpOnly; SameSite=Strict";
                final String refreshCookie = "refresh_token=" + newRefreshToken + "; Path=/; HttpOnly; SameSite=Strict";

                when(cookieUtil.addAuthCookie(newAccessToken)).thenReturn(accessCookie);
                when(cookieUtil.addRefreshCookie(newRefreshToken)).thenReturn(refreshCookie);

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_COOKIE_NAME,
                                                oldRefreshToken)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                                .andExpect(jsonPath("$.data.token").value(newAccessToken))
                                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, accessCookie, refreshCookie));

                verify(authService, times(1)).refresh(oldRefreshToken);
                verify(cookieUtil, times(1)).addAuthCookie(newAccessToken);
                verify(cookieUtil, times(1)).addRefreshCookie(newRefreshToken);
        }

        @Test
        void refreshSuccessWithRequestBody() throws Exception {
                final String oldRefreshToken = "old.refresh.token.from.body";
                final String newAccessToken = "new.jwt.token";
                final String newRefreshToken = "new.refresh.token";

                final AuthResponse refreshedResponse = AuthResponse.builder()
                                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                                .username("Admin Sawit")
                                .role(Role.ADMIN)
                                .token(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();

                when(authService.refresh(oldRefreshToken)).thenReturn(refreshedResponse);

                final String accessCookie = "auth_token=" + newAccessToken + "; Path=/; HttpOnly; SameSite=Strict";
                final String refreshCookie = "refresh_token=" + newRefreshToken + "; Path=/; HttpOnly; SameSite=Strict";

                when(cookieUtil.addAuthCookie(newAccessToken)).thenReturn(accessCookie);
                when(cookieUtil.addRefreshCookie(newRefreshToken)).thenReturn(refreshCookie);

                final RefreshRequest requestBody = RefreshRequest.builder()
                                .refreshToken(oldRefreshToken)
                                .build();

                mockMvc.perform(post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                                .andExpect(jsonPath("$.data.token").value(newAccessToken))
                                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, accessCookie, refreshCookie));

                verify(authService, times(1)).refresh(oldRefreshToken);
        }

        @Test
        void refreshMissingTokenThrows() throws Exception {
                mockMvc.perform(post("/api/auth/refresh"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Refresh token is missing"));

                verify(authService, never()).refresh(any());
                verify(cookieUtil, never()).addAuthCookie(any());
        }

        @Test
        void refreshInvalidOrExpiredTokenThrows() throws Exception {
                final String badToken = "expired.or.invalid.refresh.token";
                when(authService.refresh(badToken)).thenThrow(new InvalidCredentialException());

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_COOKIE_NAME, badToken)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid credentials"));

                verify(authService, times(1)).refresh(badToken);
                verify(cookieUtil, never()).addAuthCookie(any());
                verify(cookieUtil, never()).addRefreshCookie(any());
        }
}