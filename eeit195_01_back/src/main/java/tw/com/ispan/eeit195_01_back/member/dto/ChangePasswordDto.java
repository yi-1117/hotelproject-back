package tw.com.ispan.eeit195_01_back.member.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private Integer memberId;
    private String oldPassword;
    private String newPassword;
}


