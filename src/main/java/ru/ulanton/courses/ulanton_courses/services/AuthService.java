package ru.ulanton.courses.ulanton_courses.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.response.JwtResponse;
import ru.ulanton.courses.ulanton_courses.dto.request.LoginRequest;
import ru.ulanton.courses.ulanton_courses.dto.response.UserDTO;
import ru.ulanton.courses.ulanton_courses.exceptions.BadRequestException;
import ru.ulanton.courses.ulanton_courses.models.User;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    public JwtResponse login(LoginRequest request){
        try{
            logger.debug("Login attempt for user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            userService.updateLastLogin(user.getUsername());

            String token = jwtService.generateToken(user);

            String role = user.getRoles().isEmpty() ? "USER" :
                    user.getRoles().iterator().next().name();

            logger.debug("User logged in successfully: {}", user.getUsername());

            return new JwtResponse(token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    role);

        } catch (BadCredentialsException e) {
            logger.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new BadRequestException("Неверный логин или пароль");
        } catch (Exception e) {
            logger.error("Login error for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Ошибка ввода", e);
        }
    }

    public void logout(){
        SecurityContextHolder.clearContext();
        logger.debug("User logged out");
    }

    public UserDTO getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())){
            Object principal = authentication.getPrincipal();
            if(principal instanceof User){
                User user = (User) principal;
                logger.debug("Current user: {}", user.getUsername());
                return new UserDTO(user);
            }
        }
        return null;
    }
}