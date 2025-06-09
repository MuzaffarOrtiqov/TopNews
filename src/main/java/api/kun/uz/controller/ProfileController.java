package api.kun.uz.controller;

import api.kun.uz.dto.auth.AuthRequestDTO;
import api.kun.uz.dto.auth.AuthResponseDTO;
import api.kun.uz.dto.profile.ProfileDTO;
import api.kun.uz.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;


}
