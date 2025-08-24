package chandu.in.Authfy.service;

import chandu.in.Authfy.dto.ProfileRequest;
import chandu.in.Authfy.dto.ProfileResponse;

public interface ProfileServiceInterface {

    ProfileResponse registerUser(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

}
