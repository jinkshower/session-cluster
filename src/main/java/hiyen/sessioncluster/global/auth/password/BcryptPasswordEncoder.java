package hiyen.sessioncluster.global.auth.password;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

	public String encode(String plainText) {
		return BCrypt.hashpw(plainText, BCrypt.gensalt());
	}

	public boolean matches(String plainText, String hashed) {
		return BCrypt.checkpw(plainText, hashed);
	}
}
