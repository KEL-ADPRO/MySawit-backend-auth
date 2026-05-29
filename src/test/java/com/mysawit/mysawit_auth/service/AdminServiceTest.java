package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.validator.AdminValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private TokenAuthorizationService tokenAuthorizationService;

    @Mock
    private AuthRepository authRepository;

    @Spy
    private AuthResponseMapper authResponseMapper =  new AuthResponseMapper();

    @Mock
    private AdminValidator adminValidator;

    @InjectMocks
    private AdminServiceImpl adminService;

    private static final UUID ADMIN_ID  = UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6");
    private static final UUID BURUH_ID  = UUID.fromString("136d7ac3-2ee7-4e74-8225-e9062540a6c8");
    private static final UUID MANDOR_ID = UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6");
    private static final String ADMIN_TOKEN = "valid.admin.jwt";

    private User adminUser;
    private User buruhUser;
    private User mandorUser;
    private User supirUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(ADMIN_ID)
                .username("Admin")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        buruhUser = User.builder()
                .id(BURUH_ID)
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com").role(Role.BURUH).build();

        mandorUser = User.builder()
                .id(MANDOR_ID)
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        supirUser = User.builder()
                .id(UUID.randomUUID())
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .role(Role.SUPIR)
                .build();
    }

    private void mockValidAdminToken() {
        when(tokenAuthorizationService.requireRole(ADMIN_TOKEN, Role.ADMIN)).thenReturn(ADMIN_ID);
    }

    private void mockInvalidToken() {
        when(tokenAuthorizationService.requireRole(eq(ADMIN_TOKEN), any())).thenThrow(new InvalidCredentialException());
    }

    @Test
    void getUsersWithFiltersNoFilters() {
        mockValidAdminToken();
        when(authRepository.findAll()).thenReturn(List.of(adminUser, buruhUser, mandorUser, supirUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, null, null, null);

        assertEquals(4, result.size());
        verify(authRepository).findAll();
    }

    @Test
    void getUsersWithFiltersNameOnly() {
        mockValidAdminToken();
        when(authRepository.findByName("Usep")).thenReturn(List.of(buruhUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, "Usep", null, null);

        assertEquals(1, result.size());
        assertEquals("Usep", result.getFirst().getName());
    }

    @Test
    void getUsersWithFiltersEmailOnly() {
        mockValidAdminToken();
        when(authRepository.findByEmail("usep@gmail.com")).thenReturn(Optional.of(buruhUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, null, "usep@gmail.com", null);

        assertEquals(1, result.size());
        assertEquals("usep@gmail.com", result.getFirst().getEmail());
    }

    @Test
    void getUsersWithFiltersRoleOnly() {
        mockValidAdminToken();
        when(authRepository.findByRole(Role.BURUH)).thenReturn(List.of(buruhUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, null, null, Role.BURUH);

        assertEquals(1, result.size());
        assertEquals(Role.BURUH, result.getFirst().getRole());
    }

    @Test
    void getUsersWithFiltersNameAndEmail() {
        mockValidAdminToken();
        when(authRepository.findByNameAndEmail("Usep", "usep@gmail.com")).thenReturn(List.of(buruhUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, "Usep", "usep@gmail.com", null);

        assertEquals(1, result.size());
        verify(authRepository).findByNameAndEmail("Usep", "usep@gmail.com");
    }

    @Test
    void getUsersWithFiltersNameAndRole() {
        mockValidAdminToken();
        when(authRepository.findByNameAndRole("Burhan", Role.MANDOR)).thenReturn(List.of(mandorUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, "Burhan", null, Role.MANDOR);

        assertEquals(1, result.size());
        verify(authRepository).findByNameAndRole("Burhan", Role.MANDOR);
    }

    @Test
    void getUsersWithFiltersEmailAndRole() {
        mockValidAdminToken();
        when(authRepository.findByEmailAndRole("burhan@gmail.com", Role.MANDOR)).thenReturn(List.of(mandorUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, null, "burhan@gmail.com", Role.MANDOR);

        assertEquals(1, result.size());
        verify(authRepository).findByEmailAndRole("burhan@gmail.com", Role.MANDOR);
    }

    @Test
    void getUsersWithFiltersEmailAndRole_mismatchReturnsEmpty() {
        mockValidAdminToken();
        when(authRepository.findByEmailAndRole("burhan@gmail.com", Role.BURUH)).thenReturn(List.of());

        assertTrue(adminService.getUsersWithFilters(ADMIN_TOKEN, null, "burhan@gmail.com", Role.BURUH).isEmpty());
    }

    @Test
    void getUsersWithFiltersAllThreeFilters() {
        mockValidAdminToken();
        when(authRepository.findByNameAndEmailAndRole("Burhan", "burhan@gmail.com", Role.MANDOR)).thenReturn(List.of(mandorUser));

        final List<UserSummary> result = adminService.getUsersWithFilters(ADMIN_TOKEN, "Burhan", "burhan@gmail.com", Role.MANDOR);

        assertEquals(1, result.size());
        verify(authRepository).findByNameAndEmailAndRole("Burhan", "burhan@gmail.com", Role.MANDOR);
        verify(authRepository, never()).findAll();
    }

    @Test
    void getUsersWithFiltersAllThreeFilters_noMatch() {
        mockValidAdminToken();
        when(authRepository.findByNameAndEmailAndRole("unknown", "unknown@gmail.com", Role.ADMIN)).thenReturn(List.of());

        assertTrue(adminService.getUsersWithFilters(ADMIN_TOKEN, "unknown", "unknown@gmail.com", Role.ADMIN).isEmpty());
    }

    @Test
    void getUsersWithFiltersNullToken() {
        when(tokenAuthorizationService.requireRole(null, Role.ADMIN)).thenThrow(new InvalidCredentialException());

        assertThrows(InvalidCredentialException.class, () -> adminService.getUsersWithFilters(null, null, null, null));

        verify(authRepository, never()).findAll();
    }

    @Test
    void getUsersWithFiltersBlacklistedToken() {
        mockInvalidToken();

        assertThrows(InvalidCredentialException.class, () -> adminService.getUsersWithFilters(ADMIN_TOKEN, null, null, null));

        verify(authRepository, never()).findAll();
    }

    @Test
    void getUsersWithFiltersNonAdminRole() {
        when(tokenAuthorizationService.requireRole(ADMIN_TOKEN, Role.ADMIN)).thenThrow(new IllegalArgumentException("Access denied: ADMIN role required"));

        assertThrows(IllegalArgumentException.class, () -> adminService.getUsersWithFilters(ADMIN_TOKEN, null, null, null));

        verify(authRepository, never()).findAll();
    }


    @Test
    void getUserByIdSuccess() {
        mockValidAdminToken();
        when(authRepository.findById(BURUH_ID)).thenReturn(buruhUser);

        final UserDetailResponse result = adminService.getUserById(ADMIN_TOKEN, BURUH_ID);

        assertNotNull(result);
        assertEquals(BURUH_ID, result.getUserId());
        assertEquals(Role.BURUH, result.getRole());
    }

    @Test
    void getUserByIdMandorIncludesNomorSertifMandor() {
        mockValidAdminToken();
        when(authRepository.findById(MANDOR_ID)).thenReturn(mandorUser);

        final UserDetailResponse result = adminService.getUserById(ADMIN_TOKEN, MANDOR_ID);

        assertEquals("CERT-001", result.getNomorSertifMandor());
    }

    @Test
    void getUserByIdBuruhWithMandorAssignment() {
        buruhUser.setMandorId(MANDOR_ID);
        mockValidAdminToken();
        when(authRepository.findById(BURUH_ID)).thenReturn(buruhUser);
        when(authRepository.findById(MANDOR_ID)).thenReturn(mandorUser);

        final UserDetailResponse result = adminService.getUserById(ADMIN_TOKEN, BURUH_ID);

        assertEquals(Role.MANDOR, result.getMandor().getRole());
        assertEquals(MANDOR_ID, result.getMandor().getUserId());
    }

    @Test
    void getUserByIdNotFound() {
        mockValidAdminToken();
        final UUID ghostId = UUID.randomUUID();
        when(authRepository.findById(ghostId)).thenReturn(null);

        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.getUserById(ADMIN_TOKEN, ghostId));

        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getUserByIdNullToken() {
        when(tokenAuthorizationService.requireRole(null, Role.ADMIN)).thenThrow(new InvalidCredentialException());

        assertThrows(InvalidCredentialException.class, () -> adminService.getUserById(null, BURUH_ID));

        verify(authRepository, never()).findById(any());
    }

    @Test
    void getUserByIdBlacklistedToken() {
        mockInvalidToken();

        assertThrows(InvalidCredentialException.class, () -> adminService.getUserById(ADMIN_TOKEN, BURUH_ID));

        verify(authRepository, never()).findById(any());
    }

    @Test
    void getUserByIdNonAdmin() {
        when(tokenAuthorizationService.requireRole(ADMIN_TOKEN, Role.ADMIN)).thenThrow(new IllegalArgumentException("Access denied: ADMIN role required"));

        assertThrows(IllegalArgumentException.class, () -> adminService.getUserById(ADMIN_TOKEN, BURUH_ID));

        verify(authRepository, never()).findById(any());
    }

    @Test
    void getUserByIdAdminCanFetchSelf() {
        mockValidAdminToken();
        when(authRepository.findById(ADMIN_ID)).thenReturn(adminUser);

        final UserDetailResponse result = adminService.getUserById(ADMIN_TOKEN, ADMIN_ID);

        assertEquals(ADMIN_ID, result.getUserId());
        assertEquals(Role.ADMIN, result.getRole());
    }
}