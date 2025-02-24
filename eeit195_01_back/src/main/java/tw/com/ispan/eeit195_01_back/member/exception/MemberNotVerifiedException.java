package tw.com.ispan.eeit195_01_back.member.exception;

public class MemberNotVerifiedException extends RuntimeException {

    public MemberNotVerifiedException(String message) {
        super(message); // 傳遞錯誤訊息
    }

    public MemberNotVerifiedException(String message, Throwable cause) {
        super(message, cause); // 傳遞錯誤訊息和根本原因
    }
}