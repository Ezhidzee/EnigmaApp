package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationModel {

    private String nickname;

    private String password;

    private String publicKey;

    public AuthenticationModel(String nickname, String password, String publicKey) {
        this.nickname = nickname;
        this.password = password;
        this.publicKey = publicKey;
    }
}
