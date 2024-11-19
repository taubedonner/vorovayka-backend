package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("SELECT r FROM RefreshToken r WHERE r.token = :t AND r.fingerprint = :f AND r.expiresIn > CURRENT_TIMESTAMP")
    Optional<RefreshToken> findValidByTokenAndFingerprint(@Param("t") String token, @Param("f") String fingerprint);

    List<RefreshToken> findByUserId(UUID userId);

    Long countByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByToken(String token);

    void deleteByIdAndUserId(UUID id, UUID userId);

    void deleteByUserIdAndFingerprint(UUID userId, String fingerprint);

    void deleteByIdNot(@NonNull UUID id);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :u AND r.fingerprint != :fg")
    void deleteByUserIdExceptCurrentFingerprint(@Param("u") UUID userId, @Param("fg") String fingerprint);
}
