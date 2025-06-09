package api.kun.uz.service;

import api.kun.uz.config.CustomUserDetails;
import api.kun.uz.dto.auth.AuthRequestDTO;
import api.kun.uz.dto.auth.AuthResponseDTO;
import api.kun.uz.dto.profile.ProfileDTO;
import api.kun.uz.entity.ProfileEntity;
import api.kun.uz.enums.AppLanguage;
import api.kun.uz.exception.AppBadException;
import api.kun.uz.repository.ProfileRepository;
import api.kun.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;


    public ProfileEntity findProfileById(String profileId, AppLanguage lang) {
        Optional<ProfileEntity> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            return profile.get();
        }
        throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
    }
}