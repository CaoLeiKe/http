package com.zoomdu.utils.http.cookie;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * cookie储存器,
 * 为避免每次加减cookie会上锁，故自定义没有锁的储存器
 * 提示性能
 *
 * @author 曹磊科
 * @date 2018/05/10 12:36
 */
public class DefaultCookieStore implements CookieStore, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Cookie> cookies;

    public DefaultCookieStore() {
        this.cookies = new ArrayList<>();
    }

    @Override
    public void addCookie(final Cookie cookie) {
        if (cookie != null) {
            cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
                cookies.add(cookie);
            }
        }
    }

    public void addCookies(final Cookie[] cookies) {
        if (cookies != null && cookies.length > 0) {
            for (final Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }

    @Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        boolean removed = false;
        for (final Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public void clear() {
        cookies.clear();
    }

    @Override
    public String toString() {
        return cookies.toString();
    }
}
