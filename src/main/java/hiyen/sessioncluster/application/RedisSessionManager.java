package hiyen.sessioncluster.application;

import hiyen.sessioncluster.domain.Member;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSessionManager implements SessionManager {
	private final RedisTemplate<String, Object> redisTemplate;
	private final SessionIdGenerator sessionIdGenerator;

	public RedisSessionManager(
		final RedisTemplate<String, Object> redisTemplate,
		final SessionIdGenerator sessionIdGenerator
	) {
		this.redisTemplate = redisTemplate;
		this.sessionIdGenerator = sessionIdGenerator;
	}

	@Override
	public String establish(final Member member) {

		String sessionId = sessionIdGenerator.generate();
		redisTemplate.opsForValue().set(sessionId, member, 60, TimeUnit.MINUTES);

		return sessionId;
	}

	@Override
	public boolean isExist(final String sessionId) {
		return redisTemplate.opsForValue().get(sessionId) != null;
	}

	@Override
	public Member getMember(final String sessionId) {
		return (Member) redisTemplate.opsForValue().get(sessionId);
	}
}
