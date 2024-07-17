package hiyen.sessioncluster.member.intergration;

import static org.assertj.core.api.Assertions.*;

import hiyen.sessioncluster.member.ui.dto.request.MemberCreateRequest;
import hiyen.sessioncluster.member.ui.dto.request.MemberLoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Disabled
public class SessionClusteringTest {

	@Container
	public static DockerComposeContainer<?> compose =
		new DockerComposeContainer<>(new File("docker-compose.yml"));
	@BeforeAll
	static void beforeAll() {
		compose.start();
	}

	@AfterAll
	static void afterAll() {
		compose.stop();
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	void testSessionClustering() {
		//회원 가입
		MemberCreateRequest memberCreateRequest = new MemberCreateRequest("exmaple@example.com", "password1!", "jinkshower");
		register(memberCreateRequest, 8080);

		//로그인
		MemberLoginRequest memberLoginRequest = new MemberLoginRequest("example@example.com", "password1!");
		login(memberLoginRequest, 8080);

		// 인스턴스 1에 체크 요청
		ExtractableResponse<Response> check1 = check(8080);
		ExtractableResponse<Response> check2 = check(8081);

		// 세션 ID가 동일한지 확인
		assertThat(check1.statusCode()).isEqualTo(200);
		assertThat(check2.statusCode()).isEqualTo(200);

		assertThat(check1.cookie("JSESSIONID")).isEqualTo(check2.cookie("JSESSIONID"));
	}

	private ExtractableResponse<Response> register(final MemberCreateRequest request, int port) {
		return RestAssured.given().log().all()
			.port(port)
			.body(request)
			.contentType("application/json")
			.when().post("/api/members/register")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> login(final MemberLoginRequest request, int port) {
		return RestAssured.given().log().all()
			.port(port)
			.body(request)
			.contentType("application/json")
			.when().post("/api/members/login")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> check(int port) {
		return RestAssured.given().log().all()
			.port(port)
			.when().get("/api/members/check")
			.then().log().all()
			.extract();
	}
}
