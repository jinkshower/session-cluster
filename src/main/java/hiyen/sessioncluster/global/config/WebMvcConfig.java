package hiyen.sessioncluster.global.config;

import hiyen.sessioncluster.global.auth.AuthInterceptor;
import hiyen.sessioncluster.global.auth.AuthMemberArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private final AuthInterceptor authInterceptor;
	private final AuthMemberArgumentResolver authMemberArgumentResolver;

	public WebMvcConfig(final AuthInterceptor authInterceptor,
		final AuthMemberArgumentResolver authMemberArgumentResolver) {
		this.authInterceptor = authInterceptor;
		this.authMemberArgumentResolver = authMemberArgumentResolver;
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
			.excludePathPatterns("/", "/api/members/register", "/api/members/login");
	}

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authMemberArgumentResolver);
	}
}