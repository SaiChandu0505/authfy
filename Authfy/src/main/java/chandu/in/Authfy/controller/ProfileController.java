package chandu.in.Authfy.controller;

import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.service.ProfileServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceInterface profileServiceInterface;

    @PostMapping("/user-register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> userRegister(@Valid @RequestBody ProfileRequest request) {
        try {
            profileServiceInterface.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }

    }

    @GetMapping("/test")
    public String test() {
        return "Auth is working";
    }
}
