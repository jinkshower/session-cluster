package hiyen.sessioncluster.member.ui;

import static org.assertj.core.api.Assertions.*;

import hiyen.sessioncluster.member.common.AcceptanceTest;
import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import hiyen.sessioncluster.member.ui.dto.request.MemberLoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberRestControllerTest extends AcceptanceTest {

	@Nested
	@DisplayName("회원 가입")
	class Register {

		final String email = "example@example.com";
		final String password = "Password1";
		final String name = "jinkshower";

		@Test
		@DisplayName("성공")
		void success() {
			// given
			MemberCreateRequest request = new MemberCreateRequest(email, password, name);

			// when
			ExtractableResponse<Response> response = register(request);

			// then
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Location")).contains("/api/members/");
		}

		@ParameterizedTest
		@DisplayName("비정상 이메일로 가입시 실패한다.")
		@ValueSource(strings = {"", "s", "tignibimmg", "eifi@eianf", "example@.com", "example@com", "example@com."})
		void fail_wrongEmail(final String wrongEmail) {
			// given
			final MemberCreateRequest request = new MemberCreateRequest(wrongEmail, password, name);

			// when
			final ExtractableResponse<Response> response = register(request);

			// then
			assertThat(response.statusCode()).isEqualTo(400);
		}

		@ParameterizedTest
		@DisplayName("4자 이상 소문자, 대문자, 숫자 조합이 아닌 비밀번호로 가입시 실패한다.")
		@ValueSource(strings = {"", "p2P", "PASSWORD", "password!", "PASSWORD1", "password1", "PASSWORD1!"})
		void fail_wrongPassword(final String wrongPassword) {
			// given
			final MemberCreateRequest request = new MemberCreateRequest(email, wrongPassword, name);

			// when
			final ExtractableResponse<Response> response = register(request);

			// then
			assertThat(response.statusCode()).isEqualTo(400);
		}

		@ParameterizedTest
		@DisplayName("2자 이상 10자 이하가 아닌 이름으로 가입시 실패한다.")
		@ValueSource(strings = {"", "s", "tignibimmg1"})
		void fail_wrongName(final String wrongName) {
			// given
			final MemberCreateRequest request = new MemberCreateRequest(email, password, wrongName);

			// when
			final ExtractableResponse<Response> response = register(request);

			// then
			assertThat(response.statusCode()).isEqualTo(400);
		}
	}

	@Nested
	@DisplayName("로그인")
	class Login {

		final String email = "example@example.com";
		final String password = "Password1";
		final String name = "jinkshower";

		@BeforeEach
		void setUp() {
			final MemberCreateRequest request = new MemberCreateRequest(email, password, name);
			register(request);
		}

		@Test
		@DisplayName("성공")
		void success() {
			// given
			final MemberLoginRequest request = new MemberLoginRequest(email, password);

			// when
			ExtractableResponse<Response> login = login(request);

			// then
			assertThat(login.statusCode()).isEqualTo(200);
			assertThat(login.cookie("JSESSIONID")).isNotNull();
		}
	}

	private ExtractableResponse<Response> login(final MemberLoginRequest request) {
		return RestAssured.given().log().all()
				.body(request)
				.contentType("application/json")
				.when().post("/api/members/login")
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> register(final MemberCreateRequest request) {
		return RestAssured.given().log().all()
				.body(request)
				.contentType("application/json")
				.when().post("/api/members/register")
				.then().log().all()
				.extract();
	}
}
