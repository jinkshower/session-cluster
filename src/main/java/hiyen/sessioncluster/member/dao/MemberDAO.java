package hiyen.sessioncluster.member.dao;

import hiyen.sessioncluster.member.domain.Member;
import java.util.Optional;

public interface MemberDAO {

	Member save(Member member);

	Optional<Member> findById(Long id);

	Optional<Member> findByEmail(String email);
}
