package com.pawtrip.app;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF = "pawtrip_session";
    private SharedPreferences prefs;

    public UserSession(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveUser(String name, String email, String password) {
        prefs.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .putBoolean("logged_in", true)
            .apply();
    }

    public void setLoggedIn(boolean val) {
        prefs.edit().putBoolean("logged_in", val).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("logged_in", false);
    }

    public boolean hasAccount() {
        return !prefs.getString("email", "").isEmpty();
    }

    public String getName()  { return prefs.getString("name", ""); }
    public String getEmail() { return prefs.getString("email", ""); }

    public boolean checkPassword(String input) {
        return prefs.getString("password", "").equals(input);
    }

    public void logout() {
        prefs.edit().putBoolean("logged_in", false).apply();
    }

    /** Saves an arbitrary extra string (e.g. URI path for photo or proof doc). */
    public void saveExtra(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    /** Retrieves an arbitrary extra string saved via saveExtra(). */
    public String getExtra(String key) {
        return prefs.getString(key, "");
    }

    public void clear() { prefs.edit().clear().apply(); }
}