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

class MemberRestControllerTest extends AcceptanceTest {

	@Nested
	@DisplayName("회원 가입")
	class Register {

		final String email = "example@example.com";
		final String password = "password1!";
		final String name = "jinkshower";

		@Test
		@DisplayName("성공한다.")
		void success() {
			// given
			MemberCreateRequest request = new MemberCreateRequest(email, password, name);

			// when
			ExtractableResponse<Response> response = register(request);

			// then
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Location")).contains("/api/members/");
		}
	}

	@Nested
	@DisplayName("로그인")
	class Login {

		final String email = "example@example.com";
		final String password = "password1!";
		final String name = "jinkshower";

		@BeforeEach
		void setUp() {
			final MemberCreateRequest request = new MemberCreateRequest(email, password, name);
			register(request);
		}

		@Test
		@DisplayName("성공한다.")
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
