package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponseModel {

    private String nickname;

    private String token;

    private String phoneNumber;

    private Integer id;

    public AuthenticationResponseModel(String nickname, String token, String phoneNumber, Integer id) {
        this.nickname = nickname;
        this.token = token;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }
}
