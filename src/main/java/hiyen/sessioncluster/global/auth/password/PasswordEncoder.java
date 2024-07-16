package hiyen.sessioncluster.global.auth.password;

public interface PasswordEncoder {

	String encode(String plainText);

	boolean matches(String plainText, String hashed);
}
