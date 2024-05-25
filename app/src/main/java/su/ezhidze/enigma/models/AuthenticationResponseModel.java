package su.ezhidze.enigma.models;

public class AuthenticationResponseModel {

    private Integer id;

    private String nickname;

    private String phoneNumber;

    private String image;

    private String publicKey;

    private String token;

    public AuthenticationResponseModel(UserResponseModel userResponseModel) {
        id = userResponseModel.getId();
        nickname = userResponseModel.getNickname();
        phoneNumber = userResponseModel.getPhoneNumber();
        image = userResponseModel.getImage();
        publicKey = userResponseModel.getPublicKey();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
