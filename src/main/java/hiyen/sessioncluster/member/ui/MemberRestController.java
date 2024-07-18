package hiyen.sessioncluster.member.ui;

import hiyen.sessioncluster.global.auth.AuthEmail;
import hiyen.sessioncluster.global.auth.AuthMember;
import hiyen.sessioncluster.global.auth.AuthResponse;
import hiyen.sessioncluster.global.auth.session.SessionManager;
import hiyen.sessioncluster.member.application.MemberLoginService;
import hiyen.sessioncluster.member.application.MemberService;
import hiyen.sessioncluster.member.domain.Member;
import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import hiyen.sessioncluster.member.ui.dto.request.MemberLoginRequest;
import hiyen.sessioncluster.member.ui.dto.response.MemberResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRestController {

	private final MemberService memberService;
	private final MemberLoginService memberLoginService;
	private final SessionManager sessionManager;

	@PostMapping("/register")
	public ResponseEntity<MemberResponse> register(@RequestBody @Validated final MemberCreateRequest request) {

		final Member created = memberService.register(request);
		final MemberResponse response = new MemberResponse(created.getId(), created.getEmail());
		final URI uri = URI.create("/api/members/" + created.getId());

		return ResponseEntity.created(uri)
			.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody final MemberLoginRequest request, final
	HttpServletResponse response) {

		final Member logined = memberLoginService.login(request);

		final String sessionId = sessionManager.establish(logined);

		final Cookie cookie = new Cookie(SessionManager.SESSION_KEY, sessionId);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return ResponseEntity.ok()
			.body(new AuthResponse(logined.getEmail(), sessionId));
	}

	@GetMapping("/check")
	public ResponseEntity<MemberResponse> check(@AuthMember AuthEmail authEmail) {

		final Member found = memberService.check(authEmail.email());
		final MemberResponse response = new MemberResponse(found.getId(), found.getEmail());

		return ResponseEntity.ok()
			.body(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logout(@AuthMember AuthEmail authEmail,
		final HttpServletRequest request, final HttpServletResponse response) {

		final String sessionId = sessionManager.extractSessionId(request);
		sessionManager.invalidate(sessionId);

		final Cookie cookie = new Cookie(SessionManager.SESSION_KEY, null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		return ResponseEntity.ok()
			.body(new AuthResponse(authEmail.email(), sessionId));
	}
}
