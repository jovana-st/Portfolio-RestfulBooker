package config;

public class AuthConfig {

    public static final String USERNAME = System.getProperty("auth.username", "admin");
    public static final String PASSWORD = System.getProperty("auth.password", "password123");

}