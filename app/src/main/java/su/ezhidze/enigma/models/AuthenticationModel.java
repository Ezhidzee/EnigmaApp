package su.ezhidze.enigma.models;

public class AuthenticationModel {

    private String nickname;

    private String password;

    private String publicKey;

    public AuthenticationModel(String nickname, String password, String publicKey) {
        this.nickname = nickname;
        this.password = password;
        this.publicKey = publicKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
