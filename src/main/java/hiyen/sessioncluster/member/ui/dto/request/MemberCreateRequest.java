package hiyen.sessioncluster.member.ui.dto.request;

public record MemberCreateRequest(
	String email,
	String password,
	String name
) {

}