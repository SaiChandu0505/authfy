package chandu.in.Authfy.service;

import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.dto.ProfileResponse;

public interface ProfileServiceInterface {

    ProfileResponse registerUser(ProfileRequest request);

    ProfileResponse getProfile(String email);
}
