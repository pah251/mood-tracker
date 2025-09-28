package com.example.mood_tracker.service;

import com.example.mood_tracker.model.User;
import com.example.mood_tracker.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent())
        {
            user = userOptional.get();
            user.setName(name);
        }
        else
        {
            user = new User();
            user.setName(name);
            user.setEmail(email);
        }

        userRepository.save(user);
        return oAuth2User;
    }
}
