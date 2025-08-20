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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileServiceInterface {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

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
}
