package hiyen.sessioncluster.dao;

import hiyen.sessioncluster.domain.Member;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateMemberDAO implements MemberDAO {
	private static final RowMapper<Member> MEMBER_ROW_MAPPER = (rs, rowNum)
		-> new Member(
		rs.getLong("id"),
		rs.getString("email"),
		rs.getString("password"),
		rs.getString("name")
	);
	private static final String TABLE_NAME = "members";
	private static final String KEY_COLUMN_NAME = "id";

	private final JdbcTemplate jdbcTemplate;
	private final SimpleJdbcInsert jdbcInsert;

	public JdbcTemplateMemberDAO(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource)
			.withTableName(TABLE_NAME)
			.usingGeneratedKeyColumns(KEY_COLUMN_NAME);
	}

	@Override
	public Member save(final Member member) {

		SqlParameterSource params = new BeanPropertySqlParameterSource(member);
		Number key = jdbcInsert.executeAndReturnKey(params);

		return member;
	}

	@Override
	public Optional<Member> findById(final Long id) {
		return null;
	}

	@Override
	public Optional<Member> findByEmail(final String email) {
		return null;
	}
}
