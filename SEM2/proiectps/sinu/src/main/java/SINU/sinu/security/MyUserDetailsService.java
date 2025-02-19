package SINU.sinu.security;

import SINU.sinu.entities.MyUser;
import SINU.sinu.repository.MyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    private final MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Trying to load user by username: {}", username);

        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            throw new UsernameNotFoundException("Invalid username");
        }

        Optional<MyUser> userByUsername = myUserRepository.findMyUserByUsername(username);

        if (userByUsername.isEmpty()) {
            log.error("Could not find user with that username: {}", username);
            throw new UsernameNotFoundException("Invalid credentials!");
        }

        MyUser user = userByUsername.get();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getClass().getSimpleName()));

        return new MySecurityUser(user.getUsername(), user.getPassword(), true, true, true, true, grantedAuthorities,
                user.getFirstName(), user.getLastName(), user.getEmailAddress());
    }
}
