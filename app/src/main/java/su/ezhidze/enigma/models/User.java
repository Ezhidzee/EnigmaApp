package su.ezhidze.enigma.models;

import java.io.Serializable;

public class User implements Serializable {

    private String nickname;

    private String phoneNumber;

    private String image;

    private String publicKey;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public User() {
    }

    public User(final UserResponseModel userResponseModel) {
        this.nickname = userResponseModel.getNickname();
        this.phoneNumber = userResponseModel.getPhoneNumber();
        this.image = userResponseModel.getImage();
        this.publicKey = userResponseModel.getPublicKey();
        this.id = String.valueOf(userResponseModel.getId());
    }
}
