package hiyen.sessioncluster.application;

import hiyen.sessioncluster.dao.MemberDAO;
import hiyen.sessioncluster.domain.Member;
import hiyen.sessioncluster.ui.dto.request.MemberLoginRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberLoginService {
	private final MemberDAO memberDAO;
	private final SessionManager sessionManager;

	public MemberLoginService(
		final MemberDAO memberDAO,
		final SessionManager sessionManager
	) {
		this.memberDAO = memberDAO;
		this.sessionManager = sessionManager;
	}

	@Transactional
	public String login(final MemberLoginRequest request) {
		Member member = findByEmail(request.email());
		if (!member.getPassword().equals(request.password())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String sessionId = sessionManager.establish(member);
		return sessionId;
	}

	public String check(final String sessionId) {
		if (!sessionManager.isExist(sessionId)) {
			throw new IllegalArgumentException("세션이 존재하지 않습니다.");
		}

		Member member = sessionManager.getMember(sessionId);
		return member.getName();
	}


	private Member findByEmail(final String email) {
		return memberDAO.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	}
}
