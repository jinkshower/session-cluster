package hiyen.sessioncluster.member.application;

import hiyen.sessioncluster.global.auth.password.PasswordEncoder;
import hiyen.sessioncluster.member.dao.MemberDAO;
import hiyen.sessioncluster.member.domain.Member;
import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberDAO memberDAO;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member save(final MemberCreateRequest request) {
		//TODO 비밀번호 단방향 암호화가 필요함
		final Member member = new Member(
			request.email(), passwordEncoder.encode(request.password()), request.name());
		return memberDAO.save(member);
	}
}
