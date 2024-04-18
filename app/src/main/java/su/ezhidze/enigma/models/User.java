package su.ezhidze.enigma.models;

import java.io.Serializable;

public class User implements Serializable {

    private String name;

    private String phone;

    private String image;

    private String publicKey;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public User(UserResponseModel userResponseModel) {
        this.name = userResponseModel.getNickname();
        this.phone = userResponseModel.getPhoneNumber();
        this.image = "";
        this.publicKey = userResponseModel.getPublicKey();
        this.id = userResponseModel.getPublicKey();
    }
}
