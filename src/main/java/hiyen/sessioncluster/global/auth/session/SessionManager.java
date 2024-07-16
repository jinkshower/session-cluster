package hiyen.sessioncluster.global.auth.session;

import hiyen.sessioncluster.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionManager {
	String SESSION_KEY = "sessionId";

	String establish(Member member);

	boolean isExist(String sessionId);

	Member getMember(String sessionId);

	String extractSessionId(HttpServletRequest request);
}
