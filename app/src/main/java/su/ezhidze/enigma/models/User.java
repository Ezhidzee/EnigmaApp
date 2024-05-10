package su.ezhidze.enigma.models;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

    private String name;

    private String phone;

    private String image;

    private String publicKey;

    private String id;

    public User() {
    }

    public User(UserResponseModel userResponseModel) {
        this.name = userResponseModel.getNickname();
        this.phone = userResponseModel.getPhoneNumber();
        this.image = "";
        this.publicKey = userResponseModel.getPublicKey();
        this.id = String.valueOf(userResponseModel.getId());
    }
}
