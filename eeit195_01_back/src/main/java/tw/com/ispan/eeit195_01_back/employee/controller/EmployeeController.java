package tw.com.ispan.eeit195_01_back.employee.controller;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeBean;
import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeStatus;
import tw.com.ispan.eeit195_01_back.employee.dto.ChangePasswordDtoE;
import tw.com.ispan.eeit195_01_back.employee.repository.EPasswordResetRepository;
import tw.com.ispan.eeit195_01_back.employee.repository.EmployeeRepository;
import tw.com.ispan.eeit195_01_back.employee.service.EmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EPasswordResetRepository ePasswordResetRepository;

    // 一、註冊(沒密碼)

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerEmployee(@RequestBody EmployeeBean employee) {
        Map<String, Object> response = new HashMap<>();

        // 檢查 Email 是否唯一
        EmployeeBean existingEmployee = employeeService.findByEmail(employee.getEmail());
        if (existingEmployee != null) {
            // 如果找到已註冊的員工
            response.put("message", "Email is already registered");
            response.put("employeeId", existingEmployee.getEmployeeId()); // 已註冊的會員 ID
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }

        // 設定 Status
        if (!setEmployeeStatus(employee)) {
            return createErrorResponse("Invalid status value", HttpStatus.BAD_REQUEST);
        }

        // 註冊員工，注意這裡員工尚未設置 password
        EmployeeBean savedEmployee = employeeService.registerEmployee(employee);

        // 回應成功訊息，包含 employeeId
        response.put("status", "success");
        response.put("message", "Employee registered successfully");

        response.put("employeeId", savedEmployee.getEmployeeId());
        response.put("employee", employee);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // 設定並驗證 Employee 的 Status
    private boolean setEmployeeStatus(EmployeeBean employee) {
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }
        EmployeeStatus status = EmployeeStatus.valueOf(employee.getStatus().name());
        employee.setStatus(status);
        try {
            employee.setStatus(EmployeeStatus.valueOf(employee.getStatus().name()));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // 一、一 建立錯誤回應

    public ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // 二、完成註冊（密碼）
    @PostMapping("/complete-password")
    public ResponseEntity<Map<String, Object>> completeRegistration(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        if (!requestData.containsKey("employeeId") || requestData.get("employeeId") == null) {
            response.put("status", "error");
            response.put("message", "employeeId is required");
            return ResponseEntity.badRequest().body(response);
        }
        Integer employeeId;
        try {
            employeeId = Integer.parseInt(requestData.get("employeeId").toString());
        } catch (NumberFormatException e) {
            response.put("status", "error");
            response.put("message", "Invalid employeeId format");
            return ResponseEntity.badRequest().body(response);
        }
        // 取得 password，確保不為 null
        if (!requestData.containsKey("password") || requestData.get("password") == null) {
            response.put("status", "error");
            response.put("message", "Password is required");
            return ResponseEntity.badRequest().body(response);
        }
        String password = requestData.get("password").toString();

        // 根據 memberId 查詢會員
        EmployeeBean employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("employee not found"));

        // 設置密碼

        employee.setPassword(password); // 不加密，直接設置密碼

        // 更新會員資料
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);

        // 回應成功訊息
        response.put("status", "success");
        response.put("message", "Employee registration complete ");
        response.put("employeeId", employee.getEmployeeId());

        return ResponseEntity.ok(response); // 回傳完成註冊的成功訊息
    }

    // 三、員工登入
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody EmployeeBean employee) {
        Map<String, Object> response = new HashMap<>();

        // 使用 email 查找會員
        EmployeeBean foundEmployee = employeeService.findByEmail(employee.getEmail());

        // 檢查會員是否存在及密碼是否正確
        if (foundEmployee == null || !employee.getPassword().equals(foundEmployee.getPassword())) { // 明碼比對
            response.put("status", "error");
            response.put("message", "帳號或密碼錯誤");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
        // 成功回應
        response.put("status", "success");
        response.put("message", "Login successful");
        response.put("employeeId", foundEmployee.getEmployeeId()); // 新增 memberId 回應
        return ResponseEntity.ok(response); // 200 OK
    }

    // 四、修改密碼
    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDtoE changePasswordDtoE) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 查找會員
            Optional<EmployeeBean> existingEmployeeOpt = employeeService
                    .findEmployeeById(changePasswordDtoE.getEmployeeId());
            if (existingEmployeeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }
            EmployeeBean existingEmployee = existingEmployeeOpt.get();
            // 驗證舊密碼是否正確
            if (!existingEmployee.getPassword().equals(changePasswordDtoE.getOldPassword())) {

                response.put("error", "old password wrong");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            // 檢查新舊密碼是否相同
            if (existingEmployee.getPassword().equals(changePasswordDtoE.getNewPassword())) {
                response.put("error", "old password and new password can't be the same");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);
            }
            // 更新會員密碼
            existingEmployee.setPassword(changePasswordDtoE.getNewPassword());
            existingEmployee.setUpdatedAt(LocalDateTime.now()); // 記錄更新時間
            employeeService.updateEmployee(changePasswordDtoE.getEmployeeId(), existingEmployee);
            // 回傳成功訊息
            response.put("message", "password update success");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 捕獲所有異常並回傳錯誤訊息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the password");
        }
    }

    // 五、新增或更新員工詳細資料
    @PostMapping("/update/{employeeId}")
    public ResponseEntity<Map<String, Object>> updateEmployee(
            @PathVariable Integer employeeId,
            @RequestBody EmployeeBean employee) {
        Map<String, Object> response = new HashMap<>();
        log.info("EmployeeInfo: " + employee);
        employee.setEmployeeId(employeeId);
        try {
            // 確保員工存在
            EmployeeBean existingemployee = employeeService.findEmployeeById(employeeId).orElse(null);
            if (existingemployee == null)
                throw new IllegalArgumentException("Employee not found with ID: " + employeeId);

            // 檢查 email 是否重複
            isEmailUnique(employee.getEmail(), existingemployee.getEmail(), employee.getEmployeeId());

            employee.setUpdatedAt(LocalDateTime.now());

            // 儲存或更新員工詳細資料

            employeeService.updateEmployee(employeeId, employee);

            // 成功回應
            response.put("status", "success");
            response.put("message", "Details saved successfully.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 無效請求回應
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // 系統錯誤回應
            response.put("status", "error");
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 五、一 檢查 Email 是否唯一
    private boolean isEmailUnique(String newEmail, String existingEmail, Integer memberId) {
        if (newEmail != null && !newEmail.isEmpty()) {
            if (newEmail == existingEmail) {
                throw new IllegalArgumentException("The email " + newEmail + " is already in use.");
            }
        }
        return true;
    }

    // 六、透過email查詢員工
    @GetMapping("/find-by-email")
    public ResponseEntity<?> findByEmail(@RequestParam String email) {
        // 查詢員工資料
        Optional<EmployeeBean> employeeOpt = employeeRepository.findByEmail(email);

        // 如果找不到員工，回應錯誤訊息
        if (employeeOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "No employee found with this email");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        // 如果找到員工，回應成功訊息
        EmployeeBean employee = employeeOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("employee", employee);
        return ResponseEntity.ok(response);
    }

    // 七、透過員工Id查詢員工
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable Integer id) {
        Optional<EmployeeBean> employee = employeeService.findEmployeeById(id);
        Map<String, Object> response = new HashMap<>();

        if (employee.isPresent()) {
            response.put("status", "success");
            response.put("employee", employee);
            return ResponseEntity.ok(response); // 找到員工返回結果
        } else {
            // 員工ID不存在，返回錯誤訊息
            response.put("status", "error");
            response.put("message", "Employee with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 八、查詢所有員工
    @GetMapping("/findAllEmployee")
    public ResponseEntity<?> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EmployeeBean> employeesPage = employeeService.findAllEmployees(pageable);

            // 建立回應
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", employeesPage.getContent());
            response.put("currentPage", employeesPage.getNumber());
            response.put("totalItems", employeesPage.getTotalElements());
            response.put("totalPages", employeesPage.getTotalPages());
            return ResponseEntity.ok(response); // 返回成功回應
        } catch (Exception e) {
            // 錯誤處理
            return createErrorResponse("Error while retrieving employee details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 九、刪除員工
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();
        // 檢查員工是否存在
        if (!employeeService.findEmployeeById(id).isPresent()) {
            response.put("status", "error");
            response.put("message", "Employee not found. Unable to delete.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404
        }
        // 如果員工存在，執行刪除
        try {
            ePasswordResetRepository.deleteByEmployeeEmployeeId(id);
            employeeService.deleteEmployee(id);
            response.put("status", "success");
            response.put("message", "Employee deleted successfully.");
            return ResponseEntity.ok(response); // HTTP 200
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete employee. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500
        }
    }
}
