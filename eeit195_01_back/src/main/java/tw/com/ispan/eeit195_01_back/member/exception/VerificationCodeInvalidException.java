package tw.com.ispan.eeit195_01_back.member.exception;

public class VerificationCodeInvalidException extends RuntimeException {
    public VerificationCodeInvalidException(String message) {
        super(message);
    }
}
