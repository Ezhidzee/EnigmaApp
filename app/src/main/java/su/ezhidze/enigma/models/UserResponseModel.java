package su.ezhidze.enigma.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseModel {

    private Integer id;

    private String nickname;

    private String phoneNumber;

    private String publicKey;

    public UserResponseModel(Integer id, String nickname, String phoneNumber, String publicKey) {
        this.id = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.publicKey = publicKey;
    }
}
