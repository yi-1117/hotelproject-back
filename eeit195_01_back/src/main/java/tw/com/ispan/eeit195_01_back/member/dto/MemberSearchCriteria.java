package tw.com.ispan.eeit195_01_back.member.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data // 自动生成 getter, setter, toString, equals, hashCode 等方法
public class MemberSearchCriteria {

    private String email; // 用於查詢的 email
    private String status; // 用於查詢的狀態
    private LocalDateTime startDate; // 查詢的開始時間
    private LocalDateTime endDate; // 查詢的結束時間
    private int page = 0; // 分頁，預設為第 0 頁
    private int size = 10; // 每頁大小，預設為 10
    private String sortBy = "createdAt"; // 排序欄位，預設為 createdAt
    private String sortDir = "asc"; // 排序方向，預設為升序
}
