package hiyen.sessioncluster.global.config;

import hiyen.sessioncluster.global.auth.password.BcryptPasswordEncoder;
import hiyen.sessioncluster.global.auth.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BcryptPasswordEncoder();
	}
}
