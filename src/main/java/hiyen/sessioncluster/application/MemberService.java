package hiyen.sessioncluster.application;

import hiyen.sessioncluster.dao.MemberDAO;
import hiyen.sessioncluster.domain.Member;
import hiyen.sessioncluster.ui.dto.request.MemberCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
	private final MemberDAO memberDAO;

	public MemberService(final MemberDAO memberDAO) {
		this.memberDAO = memberDAO;
	}

	@Transactional
	public Member save(final MemberCreateRequest request) {
		//TODO 비밀번호 단방향 암호화가 필요함
		Member member = new Member(request.email(), request.password(), request.name());
		return memberDAO.save(member);
	}
}
