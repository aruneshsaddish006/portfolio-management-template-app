package com.vanguard.portal.util;

import javax.servlet.http.HttpSession;

/**
 * Utility class for session management.
 */
public class SessionUtil {

    public static String getDisplayFormat(HttpSession session) {
        String format = (String) session.getAttribute("displayFormat");
        return format != null ? format : "standard";
    }

    public static void setDisplayFormat(HttpSession session, String format) {
        session.setAttribute("displayFormat", format);
    }
}
