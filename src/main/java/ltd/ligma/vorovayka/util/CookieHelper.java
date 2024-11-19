package ltd.ligma.vorovayka.util;

import jakarta.servlet.http.Cookie;

import java.util.concurrent.TimeUnit;

public class CookieHelper {
    private static final String ATTRIBUTE_KEY_SAME_SITE = "SameSite";
    private static final String ATTRIBUTE_VALUE_SAME_SITE_NONE = "None";

    public static Cookie createHttpOnlyCookie(String key, String value, String path, Long expiryMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(expiryMs)).intValue());
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute(ATTRIBUTE_KEY_SAME_SITE, ATTRIBUTE_VALUE_SAME_SITE_NONE);
        return cookie;
    }
}
