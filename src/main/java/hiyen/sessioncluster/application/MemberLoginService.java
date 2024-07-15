package hiyen.sessioncluster.application;

import hiyen.sessioncluster.dao.MemberDAO;
import hiyen.sessioncluster.domain.Member;
import hiyen.sessioncluster.ui.dto.request.MemberLoginRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberLoginService {
	private final MemberDAO memberDAO;

	public MemberLoginService(final MemberDAO memberDAO) {
		this.memberDAO = memberDAO;
	}

	@Transactional
	public Member login(final MemberLoginRequest request) {
		Member member = findByEmail(request.email());
		if (!member.getPassword().equals(request.password())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		//TODO 세션 확립
		return member;
	}

	private Member findByEmail(final String email) {
		return memberDAO.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	}
}
