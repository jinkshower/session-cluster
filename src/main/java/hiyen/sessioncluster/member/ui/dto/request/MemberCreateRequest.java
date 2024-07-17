package hiyen.sessioncluster.member.ui.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(
	@Pattern(regexp = EMAIL_REGEX, message = "이메일 형식에 맞아야 합니다.")
	String email,
	@Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 4자 이상, 소문자, 대문자, 숫자의 조합이어야 합니다.")
	String password,
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
	String name
) {
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
	private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$";
}
