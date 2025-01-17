package hiyen.sessioncluster.global.auth;

import hiyen.sessioncluster.global.auth.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

	private final SessionManager sessionManager;

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
		final Object handler) throws Exception {

		final String sessionId = sessionManager.extractSessionId(request);

		if (!sessionManager.isExist(sessionId)) {
			throw new AuthException.FailAuthenticationMemberException();
		}

		return true;
	}
}
