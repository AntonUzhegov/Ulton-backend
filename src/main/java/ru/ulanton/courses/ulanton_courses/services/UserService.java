package ru.ulanton.courses.ulanton_courses.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.request.RegisterRequest;
import ru.ulanton.courses.ulanton_courses.dto.response.UserDTO;
import ru.ulanton.courses.ulanton_courses.exceptions.DuplicateResourceException;
import ru.ulanton.courses.ulanton_courses.exceptions.ResourceNotFoundException;
import ru.ulanton.courses.ulanton_courses.models.Role;
import ru.ulanton.courses.ulanton_courses.models.User;
import ru.ulanton.courses.ulanton_courses.repositories.UserRepository;
import java.time.LocalDateTime;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email уже используется");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new DuplicateResourceException("Имя пользователя уже занято");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.getRoles().add(Role.USER);

        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());

        return new UserDTO(user);
    }

    public UserDTO updateLastLogin(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return new UserDTO(user);
    }

    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserDTO(user);
    }

    public boolean isEmailExists(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    public User getRawUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}