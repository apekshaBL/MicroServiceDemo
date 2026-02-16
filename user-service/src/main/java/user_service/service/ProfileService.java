package user_service.service;

import user_service.entity.UserProfile;
import user_service.repository.ProfileRepository;
import user_service.dto.ProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository repository;

    public String updateProfile(Long userId, String username, ProfileRequest request) {
        // Look for existing profile or start a new one
        UserProfile profile = repository.findByUsername(username)
                .orElse(new UserProfile());

        profile.setUserId(userId);
        profile.setUsername(username);
        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());

        repository.save(profile);
        return "Profile updated successfully for " + username;
    }

    public UserProfile getProfile(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found for " + username));
    }
}