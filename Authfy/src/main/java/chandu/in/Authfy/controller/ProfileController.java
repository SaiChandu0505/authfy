package chandu.in.Authfy.controller;

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

    @PostMapping("/user-register")
    public ResponseEntity<ProfileResponse> userRegister(@Valid @RequestBody ProfileRequest request) {
        log.info("Register request received for email: {}", request.getEmail());

        ProfileResponse response = profileServiceInterface.registerUser(request);
        mailService.sendWelcomeEmail(response.getEmail(), response.getName());

        log.info("User registered successfully with email: {}", response.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Fetches profile details for the logged-in user.
     */
    @GetMapping("/user-profile")
    public ResponseEntity<ProfileResponse> getProfile(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        log.info("Fetching profile for user: {}", email);

        ProfileResponse profile = profileServiceInterface.getProfile(email);
        return ResponseEntity.ok(profile);
    }
}