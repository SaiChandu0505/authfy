package chandu.in.Authfy.controller;

import chandu.in.Authfy.Util.JwtUtil;
import chandu.in.Authfy.dto.AuthRequest;
import chandu.in.Authfy.dto.AuthResponse;
import chandu.in.Authfy.dto.ResetPasswordRequest;
import chandu.in.Authfy.service.AppUserDetailsService;
import chandu.in.Authfy.service.ProfileServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthfyController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ProfileServiceInterface profileServiceInterface;

    @PostMapping("/user-login")
    public ResponseEntity<?> userLogin(@RequestBody AuthRequest authRequest) {

        try {
            authenticate(authRequest.getEmail(), authRequest.getPassword());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(authRequest.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(authRequest.getEmail(), jwtToken));
        } catch (BadCredentialsException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (DisabledException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Authorization is failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email) {
        try {
            profileServiceInterface.sendResetOtp(email);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/rest-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            profileServiceInterface.resetPassword(request.getEmail(), request.getOtp(), request.getPassword());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public void sendOtp(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            profileServiceInterface.sendOtp(email);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public void verifyOtp(@RequestBody Map<String, Object> request,
                          @CurrentSecurityContext(expression = "authentication?.name") String email) {
        if (request.get("otp").toString() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields");
        }
        try {
            profileServiceInterface.verifyOtp(email, request.get("otp").toString());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
