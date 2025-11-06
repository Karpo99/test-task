package com.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.userservice.base.AbstractRestControllerTest;
import com.userservice.model.Token;
import com.userservice.model.dto.request.TokenInvalidateRequest;
import com.userservice.model.dto.request.TokenRefreshRequest;
import com.userservice.model.dto.request.UserLoginRequest;
import com.userservice.model.dto.request.UserRegisterRequest;
import com.userservice.service.InvalidTokenService;
import com.userservice.service.RefreshTokenService;
import com.userservice.service.TokenService;
import com.userservice.service.UserLoginService;
import com.userservice.service.UserLogoutService;
import com.userservice.service.UserRegisterService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UserControllerTest extends AbstractRestControllerTest {
    @MockitoBean
    private UserRegisterService userRegisterService;

    @MockitoBean
    private UserLoginService userLoginService;

    @MockitoBean
    private RefreshTokenService userRefreshTokenService;

    @MockitoBean
    private UserLogoutService userLogoutService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private InvalidTokenService invalidTokenService;

    @Test
    void givenValidRegisterRequest_whenRegisterUser_thenStatus_201() throws Exception {

        //Given
        UserRegisterRequest mockRequest = UserRegisterRequest.builder()
                .email("example@gmail.com")
                .firstName("Bob")
                .lastName("Boson")
                .password("password")
                .build();

        doNothing().when(userRegisterService).registerUser(any(UserRegisterRequest.class));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(""));

        //Verify
        verify(userRegisterService, times(1)).registerUser(any(UserRegisterRequest.class));
    }

    @Test
    void givenLoginRequest_WhenLoginForUser_ThenReturnToken() throws Exception {

        //Given
        UserLoginRequest loginRequest = new UserLoginRequest(
                "example@gmail.com",
                "password");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        //When
        when(userLoginService.login(any(UserLoginRequest.class))).thenReturn(mockToken);

        //Then
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(mockToken.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessTokenExpiresAt").value(mockToken.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(mockToken.getRefreshToken()));

        //Verify
        verify(userLoginService, times(1)).login(any(UserLoginRequest.class));
    }

    @Test
    void givenTokenRefreshRequest_WhenRefreshTokenForUser_ThenReturnToken() throws Exception {

        //Given
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        //When
        when(userRefreshTokenService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(mockToken);

        //Then
        mockMvc.perform(MockMvcRequestBuilders.post("/users/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(mockToken.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessTokenExpiresAt").value(mockToken.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(mockToken.getRefreshToken()));

        //Verify
        verify(userRefreshTokenService, times(1)).refreshToken(any(TokenRefreshRequest.class));
    }

    @Test
    void givenLogoutRequest_whenUserLogout_thenReturnStatus_Ok() throws Exception {

        //Given
        TokenInvalidateRequest invalidateRequest = new TokenInvalidateRequest(
                "Bearer " + mockUserToken.getAccessToken(),
                mockUserToken.getRefreshToken());

        //When
        doNothing().when(userLogoutService).logout(any(TokenInvalidateRequest.class));

        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidateRequest.accessToken())
                        .content(objectMapper.writeValueAsString(invalidateRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        //Verify
        verify(userLogoutService, times(1)).logout(any(TokenInvalidateRequest.class));
    }

    @Test
    @WithMockUser
    void givenValidToken_whenGetAuthentication_thenReturnAuthentication() throws Exception {

        //Given
        String validToken = "validToken";
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("user", "password");

        //When
        when(tokenService.getAuthentication(validToken)).thenReturn(authToken);

        //Then
        mockMvc.perform(MockMvcRequestBuilders.get("/users/authenticate").param("token", validToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.principal").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.credentials").value("password"));

        //Verify
        verify(tokenService, times(1)).getAuthentication(validToken);
    }
}
