package chandu.in.Authfy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileResponse {

    private String userId;
    private String name;
    private String email;
    private String isAccountVerified;
}
