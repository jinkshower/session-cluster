package hiyen.sessioncluster.member.doc;

import static org.apache.http.client.methods.RequestBuilder.*;
import static org.mockito.BDDMockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hiyen.sessioncluster.global.auth.AuthEmail;
import hiyen.sessioncluster.global.auth.session.SessionManager;
import hiyen.sessioncluster.member.application.MemberLoginService;
import hiyen.sessioncluster.member.application.MemberService;
import hiyen.sessioncluster.member.domain.Member;
import hiyen.sessioncluster.member.ui.MemberRestController;
import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import hiyen.sessioncluster.member.ui.dto.request.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@WebMvcTest(MemberRestController.class)
class MemberControllerDocTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private MemberLoginService memberLoginService;

	@MockBean
	private SessionManager sessionManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원가입")
	void signup() throws Exception {
		// given
		given(memberService.register(Mockito.any()))
			.willReturn(new Member(1L, "example@example.com", "Password1", "name"));

		MemberCreateRequest request = new MemberCreateRequest("example@example.com", "Password1",
			"name");
		String json = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/members/register")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.content(json))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("member-register",
				requestFields(
					fieldWithPath("email").description("이메일"),
					fieldWithPath("password").description("비밀번호"),
					fieldWithPath("name").description("이름")
				)
			));
	}

	@Test
	@DisplayName("로그인")
	void login() throws Exception {
		// given
		given(memberLoginService.login(any()))
			.willReturn(new Member(1L, "example@example.com", "Password1", "name"));
		given(sessionManager.establish(any()))
			.willReturn("sessionId");

		MemberLoginRequest request = new MemberLoginRequest("example@example.com", "Password1");

		String json = objectMapper.writeValueAsString(request);

		//when & then
		mockMvc.perform(post("/api/members/login")
			.contentType(APPLICATION_JSON)
			.accept(APPLICATION_JSON)
			.content(json))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-login",
				requestFields(
					fieldWithPath("email").description("이메일"),
					fieldWithPath("password").description("비밀번호")
				)
			));
	}

	@Test
	@DisplayName("세션 체크")
	void check() throws Exception {
		// given
		given(memberService.check(any()))
			.willReturn(new Member(1L, "example@example.com", "Password1", "name"));
		given(sessionManager.extractSessionId(any()))
			.willReturn("sessionId");
		given(sessionManager.isExist(any()))
			.willReturn(true);
		given(sessionManager.getAuthEmail(any()))
			.willReturn(new AuthEmail("example@example.com"));

		// when & then
		mockMvc.perform(get("/api/members/check")
			.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-check"));
	}

	@Test
	@DisplayName("로그아웃")
	void logout() throws Exception {
		//given
		given(sessionManager.extractSessionId(any()))
			.willReturn("sessionId");
		given(sessionManager.isExist(any()))
			.willReturn(true);
		given(sessionManager.getAuthEmail(any()))
			.willReturn(new AuthEmail("example@example.com"));


		//when & then
		mockMvc.perform(post("/api/members/logout")
			.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-logout"));
	}
}
