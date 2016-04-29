package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import org.apache.commons.lang3.StringUtils;

public class LoginAndPassword {
    private String login;
    private String passwordMd5;

    public LoginAndPassword(String login, String passwordMd5) {
        this.login = login;
        this.passwordMd5 = passwordMd5;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginAndPassword that = (LoginAndPassword) o;

        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        return passwordMd5 != null ? passwordMd5.equals(that.passwordMd5) : that.passwordMd5 == null;

    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (passwordMd5 != null ? passwordMd5.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginAndPassword{" +
                "login='" + login + '\'' +
                ", passwordMd5 present=" + StringUtils.isNotBlank(passwordMd5) +
                '}';
    }
}
