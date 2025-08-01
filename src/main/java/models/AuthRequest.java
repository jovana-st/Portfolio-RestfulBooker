package models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class AuthRequest {

    @NonNull String username;
    @NonNull String password;

}