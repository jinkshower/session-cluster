package hiyen.sessioncluster.application;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidSessionIdGenerator implements SessionIdGenerator {

	@Override
	public String generate() {
		return UUID.randomUUID().toString();
	}
}
