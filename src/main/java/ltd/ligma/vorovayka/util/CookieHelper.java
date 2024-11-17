package ltd.ligma.vorovayka.util;

import jakarta.servlet.http.Cookie;

import java.util.concurrent.TimeUnit;

public class CookieHelper {
    public static Cookie createHttpOnlyCookie(String key, String value, String path, Long expiryMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(expiryMs)).intValue());
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
