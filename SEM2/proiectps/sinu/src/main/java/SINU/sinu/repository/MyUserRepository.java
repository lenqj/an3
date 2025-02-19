package SINU.sinu.repository;

import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findUserByUsernameAndPassword(@Param("username") String username,
                                                   @Param("password") String password);
    Optional<MyUser> findMyUserByUsername(String username);

    @Query("SELECT u FROM MyUser u WHERE u.username = ?#{principal.username}")
    Optional<MyUser> findLoginUser();
    boolean existsByEmailAddress(String emailAddress);
}
