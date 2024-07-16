package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.domain.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionManager {
	String establish(Member member);

	boolean isExist(String sessionId);

	Member getMember(String sessionId);

	String extractSessionId(HttpServletRequest request);
}
