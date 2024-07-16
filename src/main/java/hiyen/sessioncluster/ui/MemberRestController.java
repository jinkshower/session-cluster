package hiyen.sessioncluster.ui;

import hiyen.sessioncluster.application.MemberLoginService;
import hiyen.sessioncluster.application.MemberService;
import hiyen.sessioncluster.domain.Member;
import hiyen.sessioncluster.global.auth.AuthMember;
import hiyen.sessioncluster.global.auth.session.SessionManager;
import hiyen.sessioncluster.ui.dto.request.MemberCreateRequest;
import hiyen.sessioncluster.ui.dto.request.MemberLoginRequest;
import hiyen.sessioncluster.ui.dto.response.MemberResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {
	private final MemberService memberService;
	private final MemberLoginService memberLoginService;

	public MemberRestController(
		final MemberService memberService,
		final MemberLoginService memberLoginService
	) {
		this.memberService = memberService;
		this.memberLoginService = memberLoginService;
	}

	@PostMapping("/register")
	public ResponseEntity<MemberResponse> save(@RequestBody final MemberCreateRequest request) {

		final Member created = memberService.save(request);
		final MemberResponse response = new MemberResponse(created.getName());
		final URI uri = URI.create("/api/members" + created.getId());

		return ResponseEntity.created(uri)
			.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody final MemberLoginRequest request, final
		HttpServletResponse response) {
		String sessionId = memberLoginService.login(request);

		Cookie cookie = new Cookie(SessionManager.SESSION_KEY, sessionId);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/check")
	public ResponseEntity<String> check(@AuthMember Member member) {
		return ResponseEntity.ok().body(member.getName());
	}
}
