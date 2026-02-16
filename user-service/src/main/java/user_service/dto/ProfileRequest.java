package user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String fullName;
    private String bio;
    private String phoneNumber;
    private String profilePictureUrl;
}