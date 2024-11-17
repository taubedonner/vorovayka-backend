package ltd.ligma.vorovayka.repository;

import ltd.ligma.vorovayka.model.RefreshToken;
import ltd.ligma.vorovayka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("SELECT r FROM RefreshToken r WHERE r.token = :t AND r.fingerprint = :f AND r.expiresIn > CURRENT_TIMESTAMP")
    Optional<RefreshToken> findValidByTokenAndFingerprint(@Param("t") UUID token, @Param("f") String fingerprint);

    Long countAllByUser(User user);

    void deleteAllByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :u")
    void deleteAllByUser(@Param("u") User user);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :u AND r.fingerprint = :fg")
    void deleteByUserAndFingerprint(@Param("u") User user, @Param("fg") String fingerprint);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :u AND r.fingerprint != :fg")
    void deleteByUserExceptCurrentFingerprint(@Param("u") User user, @Param("fg") String fingerprint);
}
