package ru.ulanton.courses.ulanton_courses.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsernameOrEmailNotFoundException extends UsernameNotFoundException {
    public UsernameOrEmailNotFoundException(String message) {
        super(message);
    }

    public UsernameOrEmailNotFoundException(String message, Throwable cause){super(message, cause);}
}
