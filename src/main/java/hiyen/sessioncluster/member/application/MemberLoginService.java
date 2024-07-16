package hiyen.sessioncluster.member.application;

import hiyen.sessioncluster.global.auth.password.PasswordEncoder;
import hiyen.sessioncluster.member.dao.MemberDAO;
import hiyen.sessioncluster.member.domain.Member;
import hiyen.sessioncluster.member.exception.MemberException;
import hiyen.sessioncluster.member.ui.dto.request.MemberLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLoginService {

	private final MemberDAO memberDAO;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member login(final MemberLoginRequest request) {
		final Member member = findByEmail(request.email());
		if (!passwordEncoder.matches(request.password(), member.getPassword())){
			throw new MemberException.FailLoginException();
		}

		log.info("login success. Member email : {}", member.getEmail());
		return member;
	}

	private Member findByEmail(final String email) {
		return memberDAO.findByEmail(email)
			.orElseThrow(MemberException.NotFoundMemberException::new);
	}
}
