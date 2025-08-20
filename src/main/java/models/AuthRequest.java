package models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AuthRequest {

    String username;
    String password;

}