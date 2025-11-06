package com.userservice.filter;

import com.userservice.model.Token;
import com.userservice.service.InvalidTokenService;
import com.userservice.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomBearerAuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Token.isBearerToken(authorizationHeader)) {

            final String jwt = Token.getJwtFromHeader(authorizationHeader);

            tokenService.verifyAndValidate(jwt);

            final String tokenId = tokenService.getId(jwt);

            invalidTokenService.checkForInvalidityOfToken(tokenId);

            final UsernamePasswordAuthenticationToken authentication = tokenService
                    .getAuthentication(jwt);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
