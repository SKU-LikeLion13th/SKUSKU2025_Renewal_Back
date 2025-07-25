package com.sku_sku.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final String baseUri = "/oauth2/authorization";

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customizeAuthorizationRequest(
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, baseUri)
                        .resolve(request), request
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customizeAuthorizationRequest(
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, baseUri)
                        .resolve(request, clientRegistrationId), request
        );
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest originalRequest, HttpServletRequest request) {
        if (originalRequest == null) return null;

        String state = request.getParameter("state"); // 프론트에서 redirect_uri 담아서 보낸 값

        Map<String, Object> additionalParameters = new HashMap<>(originalRequest.getAdditionalParameters());
        if (state != null && !state.isBlank()) {
            additionalParameters.put("redirect_uri", state);
        }

        return OAuth2AuthorizationRequest.from(originalRequest)
                .state(state != null ? state : originalRequest.getState()) // 커스텀 state 적용
                .additionalParameters(additionalParameters)
                .build();
    }
}