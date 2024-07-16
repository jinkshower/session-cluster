package hiyen.sessioncluster.member.domain;

public class Member {
	private Long id;
	private String email;
	private String password;
	private String name;

	protected Member() {
	}

	public Member(final String email, final String password, final String name) {
		this(null, email, password, name);
	}

	public Member(final Long id, final String email, final String password, final String name) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}
}
