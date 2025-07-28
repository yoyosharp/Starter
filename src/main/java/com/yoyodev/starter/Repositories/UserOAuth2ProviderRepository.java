package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.UserOAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserOAuth2Provider entity.
 */
@Repository
public interface UserOAuth2ProviderRepository extends JpaRepository<UserOAuth2Provider, Long> {
    
    /**
     * Find a UserOAuth2Provider by provider name and provider ID.
     * 
     * @param providerName The OAuth2 provider name (e.g., "google", "github")
     * @param providerId The user ID from the provider
     * @return An Optional containing the UserOAuth2Provider if found
     */
    Optional<UserOAuth2Provider> findByProviderNameAndProviderId(String providerName, String providerId);
    
    /**
     * Find all UserOAuth2Provider entries for a user.
     * 
     * @param userId The user ID
     * @return A list of UserOAuth2Provider entries
     */
    java.util.List<UserOAuth2Provider> findByUserId(Long userId);
    
    /**
     * Check if a UserOAuth2Provider exists for a provider name and provider ID.
     * 
     * @param providerName The OAuth2 provider name
     * @param providerId The user ID from the provider
     * @return True if a UserOAuth2Provider exists, false otherwise
     */
    boolean existsByProviderNameAndProviderId(String providerName, String providerId);
    
    /**
     * Find a UserOAuth2Provider by provider name and provider email.
     * This is useful for linking accounts when a user logs in with a different provider
     * but uses the same email address.
     * 
     * @param providerName The OAuth2 provider name
     * @param providerEmail The user's email from the provider
     * @return An Optional containing the UserOAuth2Provider if found
     */
    Optional<UserOAuth2Provider> findByProviderNameAndProviderEmail(String providerName, String providerEmail);
}