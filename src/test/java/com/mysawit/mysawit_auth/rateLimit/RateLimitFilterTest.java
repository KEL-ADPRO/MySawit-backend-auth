package com.mysawit.mysawit_auth.rateLimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitFilterTest {

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void doFilterInternalAllowRequest() throws ServletException, IOException {
        request.setRequestURI("/api/auth/login");
        request.setRemoteAddr("192.168.1.1");

        when(rateLimiter.tryConsume("192.168.1.1")).thenReturn(true);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternalBlockRequest() throws ServletException, IOException {
        request.setRequestURI("/api/auth/login");
        request.setRemoteAddr("192.168.1.1");

        when(rateLimiter.tryConsume("192.168.1.1")).thenReturn(false);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternalXForwardedFor() throws ServletException, IOException {
        request.setRequestURI("/api/auth/login");
        request.addHeader("X-Forwarded-For", "10.0.0.1, 192.168.1.1");

        when(rateLimiter.tryConsume("10.0.0.1")).thenReturn(true);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimiter, times(1)).tryConsume("10.0.0.1");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldNotFilterNonAuthPath() {
        request.setRequestURI("/api/users/profile");
        
        boolean shouldNotFilter = rateLimitFilter.shouldNotFilter(request);
        
        assertEquals(true, shouldNotFilter, "Should not filter non-auth paths");
    }

    @Test
    void shouldFilterAuthPath() {
        request.setRequestURI("/api/auth/register");
        
        boolean shouldNotFilter = rateLimitFilter.shouldNotFilter(request);
        
        assertEquals(false, shouldNotFilter, "Should filter auth paths");
    }
}
