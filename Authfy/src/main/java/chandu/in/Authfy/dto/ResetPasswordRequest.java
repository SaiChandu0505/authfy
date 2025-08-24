package chandu.in.Authfy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordRequest {


    @Email(message = "enter a valid email")
    @NotNull(message = "name shouldn't be empty")
    private String email;
    @NotNull(message = "otp is required")
    private String otp;
    @Size(min = 8, max = 16, message = "password should contain atleast 8 characters")
    private String password;
}
