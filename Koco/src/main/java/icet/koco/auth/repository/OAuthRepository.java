package icet.koco.auth.repository;

import icet.koco.auth.entity.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {

    // providerId로 OAuth 정보 조회
    Optional<OAuth> findByProviderId(String providerId);

    // provider + providerId로 정확히 식별
    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);

    // user_id로 OAuth 정보 조회
    Optional<OAuth> findByUserId(Long userId);
}
