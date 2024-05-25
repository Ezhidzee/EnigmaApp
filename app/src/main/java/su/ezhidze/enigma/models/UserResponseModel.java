package su.ezhidze.enigma.models;

public class UserResponseModel {

    private Integer id;

    private String nickname;

    private String phoneNumber;

    private String image;

    private String publicKey;

    public UserResponseModel(Integer id, String nickname, String phoneNumber, String publicKey) {
        this.id = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.publicKey = publicKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
