package ru.ulanton.courses.ulanton_courses.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ulanton.courses.ulanton_courses.dto.UserDto;
import ru.ulanton.courses.ulanton_courses.services.RedisSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Autowired
    private RedisSessionService redisSessionService;

    // Список публичных URL, которые не требуют проверки токена
    private final List<String> publicUrls = Arrays.asList(
            "/api/courses",
            "/api/contacts",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Проверяем, является ли URL публичным
        boolean isPublicUrl = publicUrls.stream().anyMatch(path::startsWith);

        // Для публичных URL пропускаем без проверки токена
        if (isPublicUrl) {
            logger.debug("Публичный URL: {}, пропускаем без проверки токена", path);
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                UserDto userDto = redisSessionService.getUserByToken(token);

                if (userDto != null) {
                    // Создаем аутентификацию
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDto, null, null);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Обновляем время жизни сессии
                    redisSessionService.refreshSession(token);

                    logger.debug("Пользователь {} аутентифицирован по токену", userDto.getUsername());
                } else {
                    logger.debug("Токен недействителен или сессия истекла");
                }
            } catch (Exception e) {
                // Важно: ловим все исключения, чтобы не ломать запрос
                logger.error("Ошибка при проверке токена: {}", e.getMessage());
                // Не бросаем исключение дальше, просто продолжаем без аутентификации
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Не фильтруем статические ресурсы и публичные URL
        return path.startsWith("/api/courses") ||
                path.startsWith("/api/contacts") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.equals("/") ||
                path.equals("/favicon.ico");
    }
}