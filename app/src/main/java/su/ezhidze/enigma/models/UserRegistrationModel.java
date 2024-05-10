package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationModel {

    private String nickname;

    private String phoneNumber;

    private String password;

    public UserRegistrationModel(String nickname, String phoneNumber, String password) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
