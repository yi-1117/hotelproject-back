package tw.com.ispan.eeit195_01_back.employee.dto;

import lombok.Data;

@Data
public class ChangePasswordDtoE {
    private Integer employeeId;
    private String oldPassword;
    private String newPassword;
}
