package com.sayan.auth.myauthappbeckend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        ClientRegistration google = ClientRegistration
//                .withRegistrationId("google")
//                .clientId("dummy")
//                .clientSecret("dummy")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .scope("email", "profile", "openid")
//                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//                .tokenUri("https://oauth2.googleapis.com/token")
//                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
//                .userNameAttributeName("sub")
//                .clientName("Google")
//                .build();
//
//        return new InMemoryClientRegistrationRepository(google);
//    }

}
