package hiyen.sessioncluster.exception;

public class MemberException extends RuntimeException {

	private MemberException(final String message) {
		super(message);
	}

	public static class FailLoginException extends MemberException {

		public FailLoginException() {
			super("잘못된 회원 정보를 입력하여 로그인에 실패했습니다.");
		}
	}

	public static class NotFoundMemberException extends MemberException {

		public NotFoundMemberException() {
			super("존재하지 않는 회원입니다.");
		}
	}
}
