package hiyen.sessioncluster.global.auth;

import hiyen.sessioncluster.global.auth.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {
	private final SessionManager sessionManager;

	public AuthMemberArgumentResolver(final SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthMember.class);
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter,
		final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory)
		throws Exception {

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		String sessionId = sessionManager.extractSessionId(request);
		return sessionManager.getMember(sessionId);
	}
}
