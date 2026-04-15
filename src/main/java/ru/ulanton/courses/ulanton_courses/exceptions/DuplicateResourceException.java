package ru.ulanton.courses.ulanton_courses.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object filedValue){
        super(String.format("%s с %s '%s' уже существует", resourceName, fieldName, filedValue));
    }
}
