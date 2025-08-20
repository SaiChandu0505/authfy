package chandu.in.Authfy.controller;

import chandu.in.Authfy.Util.Errorutil;
import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.dto.ProfileResponse;
import chandu.in.Authfy.service.MailService;
import chandu.in.Authfy.service.ProfileServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileServiceInterface profileServiceInterface;
    private final MailService mailService;
    private final Errorutil util;

    @PostMapping("/user-register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProfileResponse> userRegister(@Valid @RequestBody ProfileRequest request) {

        ProfileResponse response = profileServiceInterface.registerUser(request);
        mailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/user-profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return profileServiceInterface.getProfile(email);
    }
}
