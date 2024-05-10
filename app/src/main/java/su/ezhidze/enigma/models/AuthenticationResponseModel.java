package su.ezhidze.enigma.models;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
