package tw.com.ispan.eeit195_01_back.points.bean;

public class ErrorResponse {
    private String message;  // 錯誤訊息
    private int status;      // HTTP 狀態碼
    private long timestamp;  // 錯誤發生的時間戳

    // 建構子
    public ErrorResponse(int status, String message) {
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter 和 Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
