package SINU.sinu.security;

import SINU.sinu.repository.MyUserRepository;
import SINU.sinu.security.MyUserDetailsService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers(HttpMethod.GET, "/", "/api/student/all-grades", "/api/auth/login", "/api/auth/register", "/api/auth/login-error", "/css/**", "/js/**", "/images/**").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/login-error", "/css/**", "/js/**", "/images/**").permitAll();


                    authConfig.requestMatchers(HttpMethod.GET, "/api/professor/**").hasAuthority("Professor");
                    authConfig.requestMatchers(HttpMethod.POST, "/api/professor/**").hasAuthority("Professor");

                    authConfig.requestMatchers(HttpMethod.GET, "/api/student/**").hasAuthority("Student");
                    authConfig.requestMatchers(HttpMethod.POST, "/api/student/**").hasAuthority("Student");


                    authConfig.requestMatchers(HttpMethod.GET, "/api/admin/**").hasAuthority("MyUser");
                    authConfig.requestMatchers(HttpMethod.POST, "/api/admin/**").hasAuthority("MyUser");

                    authConfig.requestMatchers(HttpMethod.GET, "/api/auth/profile").authenticated();
                    authConfig.requestMatchers(HttpMethod.POST, "/api/auth/profile").authenticated();


                    authConfig.anyRequest().authenticated();
                })
                .formLogin(login -> {
                            login.loginPage("/api/auth/login");
                            login.defaultSuccessUrl("/api/auth/profile", true);
                            login.failureUrl("/api/auth/login-error");
                        }
                )
                .logout(logout -> {
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                            .logoutSuccessUrl("/api/auth/login")
                            .deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                            .permitAll();
                });

        return http.build();
    }

    @Bean
    UserDetailsService myUserDetailsService(MyUserRepository userRepository) {
        return new MyUserDetailsService(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
