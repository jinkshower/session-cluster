package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.domain.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
		redisTemplate.opsForValue().set(sessionId, member, 60, TimeUnit.SECONDS);

		return sessionId;
	}

	@Override
	public boolean isExist(final String sessionId) {
		return redisTemplate.opsForValue().get(sessionId) != null;
	}

	@Override
	public Member getMember(final String sessionId) {
		try {
			return (Member) redisTemplate.opsForValue().get(sessionId);
		} catch (Exception e) {
			throw new IllegalArgumentException("세션이 존재하지 않습니다.");
		}
	}

	@Override
	public String extractSessionId(final HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			throw new IllegalArgumentException("쿠키가 존재하지 않습니다.");
		}

		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals(SESSION_KEY))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow();
	}
}
