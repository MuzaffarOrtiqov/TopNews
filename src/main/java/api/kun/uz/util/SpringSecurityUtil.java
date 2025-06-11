package api.kun.uz.util;

import api.kun.uz.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {
    public static CustomUserDetails getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user;
    }
    public static String getCurrentProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getId();
    }
    public static String    getCurrentProfilePassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getPassword();
    }
}
