package chandu.in.Authfy.service;

import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.dto.ProfileResponse;
import chandu.in.Authfy.dto.UserEntity;
import chandu.in.Authfy.repository.UserRepository;
import chandu.in.Authfy.transformers.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileServiceInterface {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse registerUser(ProfileRequest request) {

        if (!userRepository.existsByEmail(request.getEmail())) {
            UserEntity user = profileMapper.constructUserObject(request);
            UserEntity createdUser = userRepository.save(user);
            return profileMapper.createProfileResponse(createdUser);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account Already Exist || Please try login");
        }
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return profileMapper.createProfileResponse(user);
    }

    @Override
    public void sendResetOtp(String email) {

        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        long expireTime = System.currentTimeMillis() + (15 * 60 * 1000);

        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expireTime);

        userRepository.save(existingEntity);

        try {
            mailService.sendRestOtpEmail(existingEntity.getEmail(), otp, existingEntity.getName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (existingEntity.getResetOtp() == null || !existingEntity.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid Otp");

        }
        if (existingEntity.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("Otp Expired");

        }

        existingEntity.setPassword(passwordEncoder.encode(newPassword));
        existingEntity.setResetOtp(null);
        existingEntity.setResetOtpExpireAt(0L);
        userRepository.save(existingEntity);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (existingEntity.getIsAccountVerified() != null && existingEntity.getIsAccountVerified()) {
            return;
        }
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        long expireTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expireTime);
        userRepository.save(existingEntity);

        try {
            mailService.sendRestOtpEmail(existingEntity.getEmail(), otp, existingEntity.getName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (existingEntity.getVerifyOtp() != null && !existingEntity.getVerifyOtp().equals(otp)) {
            throw new RuntimeException("Invalid Otp");

        }
        if (existingEntity.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("Otp Expired");

        }

        existingEntity.setIsAccountVerified(true);
        existingEntity.setVerifyOtp(null);
        existingEntity.setVerifyOtpExpireAt(0L);
        userRepository.save(existingEntity);
    }

}
