package chandu.in.Authfy.transformers;

import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.dto.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Component
public class ProfileMapper {
    private final PasswordEncoder passwordEncoder;

    public UserEntity constructUserObject(ProfileRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .isAccountVerified(false)
                .password(passwordEncoder.encode(request.getPassword()))
                .verifyOtp(null)
                .resetOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtpExpireAt(0L)
                .build();

    }
}
