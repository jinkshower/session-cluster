package hiyen.sessioncluster.member.application;

import hiyen.sessioncluster.global.auth.password.PasswordEncoder;
import hiyen.sessioncluster.member.dao.MemberDAO;
import hiyen.sessioncluster.member.domain.Member;
import hiyen.sessioncluster.member.exception.MemberException;
import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberDAO memberDAO;
	private final PasswordEncoder passwordEncoder;

	public Member register(final MemberCreateRequest request) {

		final Member member = new Member(
			request.email(), passwordEncoder.encode(request.password()), request.name());

		return memberDAO.save(member);
	}

	@Transactional(readOnly = true)
	public Member check(final String memberEmail) {
		return memberDAO.findByEmail(memberEmail)
			.orElseThrow(MemberException.NotFoundMemberException::new);
	}
}
