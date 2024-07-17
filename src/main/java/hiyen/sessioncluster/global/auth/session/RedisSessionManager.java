package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.global.auth.AuthException;
import hiyen.sessioncluster.global.auth.AuthEmail;
import hiyen.sessioncluster.member.domain.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSessionManager implements SessionManager {
	private final RedisTemplate<String, Object> redisTemplate;
	private final SessionIdGenerator sessionIdGenerator;

	@Override
	public String establish(final Member member) {

		final String sessionId = sessionIdGenerator.generate();
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

		final Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			throw new AuthException.FailAuthenticationMemberException();
		}

		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals(SESSION_KEY))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow();
	}
}
