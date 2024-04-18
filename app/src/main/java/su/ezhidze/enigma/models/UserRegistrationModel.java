package su.ezhidze.enigma.models;

public class UserRegistrationModel {

    private String nickname;

    private String phoneNumber;

    private String password;

    public UserRegistrationModel(String nickname, String phoneNumber, String password) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
