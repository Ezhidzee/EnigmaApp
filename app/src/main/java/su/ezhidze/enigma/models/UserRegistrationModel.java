package su.ezhidze.enigma.models;

public class UserRegistrationModel {

    private String nickname;

    private String phoneNumber;

    private String image;

    private String password;

    public UserRegistrationModel() {
    }

    public UserRegistrationModel(String nickname, String phoneNumber, String image, String password) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
