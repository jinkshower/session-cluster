package hiyen.sessioncluster.global.auth.session;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidSessionIdGenerator implements SessionIdGenerator {

	@Override
	public String generate() {
		return UUID.randomUUID().toString();
	}
}
