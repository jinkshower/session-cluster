package hiyen.sessioncluster.global.auth;

import hiyen.sessioncluster.global.auth.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	private final SessionManager sessionManager;

	public AuthInterceptor(final SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
		final Object handler) throws Exception {
		String sessionId = sessionManager.extractSessionId(request);

		if (!sessionManager.isExist(sessionId)) {
			throw new IllegalArgumentException("세션이 존재하지 않습니다.");
		}

		return true;
	}
}
