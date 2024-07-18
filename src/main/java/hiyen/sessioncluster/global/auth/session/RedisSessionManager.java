package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.global.auth.AuthException;
import hiyen.sessioncluster.global.auth.AuthEmail;
import hiyen.sessioncluster.member.domain.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSessionManager implements SessionManager {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public String establish(final Member member) {

		final String sessionId = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(sessionId, member.getEmail(), 60, TimeUnit.SECONDS);

		return sessionId;
	}

	@Override
	public boolean isExist(final String sessionId) {
		return redisTemplate.opsForValue().get(sessionId) != null;
	}

	@Override
	public AuthEmail getAuthEmail(final String sessionId) {
		try {
			String authEmail = (String) redisTemplate.opsForValue().get(sessionId);
			return new AuthEmail(authEmail);
		} catch (Exception e) {
			throw new AuthException.FailAuthenticationMemberException(e);
		}
	}

	@Override
	public String extractSessionId(final HttpServletRequest request) {

		final String sessionHeader = request.getHeader(SESSION_KEY);

		if (sessionHeader == null) {
			throw new AuthException.FailAuthenticationMemberException();
		}

		return sessionHeader;
	}

	@Override
	public void invalidate(final String sessionId) {
		redisTemplate.delete(sessionId);
	}
}
