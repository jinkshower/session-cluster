package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.global.auth.AuthEmail;
import hiyen.sessioncluster.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionManager {
	String SESSION_KEY = "Authorization";

	String establish(Member member);

	boolean isExist(String sessionId);

	AuthEmail getAuthEmail(String sessionId);

	String extractSessionId(HttpServletRequest request);

	void invalidate(String sessionId);
}
