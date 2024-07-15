package hiyen.sessioncluster.application;

import hiyen.sessioncluster.domain.Member;

public interface SessionManager {
	String establish(Member member);

	boolean isExist(String sessionId);

	Member getMember(String sessionId);
}
