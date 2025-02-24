package tw.com.ispan.eeit195_01_back.employee.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeBean;
import tw.com.ispan.eeit195_01_back.employee.exception.EmployeeNotFoundException;
import tw.com.ispan.eeit195_01_back.employee.repository.EmployeeRepository;


@Service
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    // 註冊員工
    public EmployeeBean registerEmployee(EmployeeBean employee) {
        // 檢查 email 是否已經註冊
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered: " + employee.getEmail());
        }
        // 設定創建與更新時間
        employee.setJoinDate(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        // 保存會員資料
        employeeRepository.save(employee);
        return employee;
    }

    // 根據 email 查找員工
    public EmployeeBean findByEmail(String email) {
        return employeeRepository.findByEmail(email).orElse(null);
    }
    // 根據 Id 查找員工
    public Optional<EmployeeBean> findEmployeeById(Integer employeeId) {
        return employeeRepository.findById(employeeId);
    }
    // 完成員工註冊，填寫 password
    public EmployeeBean completeRegistration(Integer employeeId, String password) {
        // 根據 employeeId 查找會員資料
        EmployeeBean employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // 設置 password
        employee.setPassword(password); // 假設你有一個 setPassword 方法來處理密碼加密
        // 更新會員資料
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);

        return employee;
    }


    // 查詢所有員工
    public Page<EmployeeBean> findAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable); // 使用 JPA 分頁功能
    }
    // 刪除員工
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    // 更新員工資料
    public EmployeeBean updateEmployee(Integer employeeId, EmployeeBean updatedEmployee) {
        if (updatedEmployee.getEmployeeId() == null) {
            throw new IllegalStateException("Employee information is missing or invalid.");
        }

        Optional<EmployeeBean> existingEmployeeOpt = employeeRepository.findById(updatedEmployee.getEmployeeId());
        if (existingEmployeeOpt.isPresent()) {
            EmployeeBean existingEmployee = existingEmployeeOpt.get();

            // 更新資料
            existingEmployee.setPassword(updatedEmployee.getPassword());
            existingEmployee.setEmail(updatedEmployee.getEmail());

            existingEmployee.setUpdatedAt(updatedEmployee.getUpdatedAt()); // 更新時間
            existingEmployee.setFullName(updatedEmployee.getFullName());
            existingEmployee.setGender(updatedEmployee.getGender());
            existingEmployee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            existingEmployee.setAddress(updatedEmployee.getAddress());
            existingEmployee.setDateOfBirth(updatedEmployee.getDateOfBirth());

            // 儲存更新後的會員資料
            return employeeRepository.save(existingEmployee);
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + employeeId);
        }
    }
    // 創造員工圖片路徑
    public String createPictureFilePath(MultipartFile file, Integer employeeId, String uploadDir) throws IOException {
         // 確保上傳路徑存在
        Path uploadRoot = Paths.get("uploads").toAbsolutePath(); // 獲取uploads目錄的絕對路徑
        Path uploadPath = uploadRoot.resolve(String.valueOf(employeeId));
        if (!uploadPath.toFile().exists()) {
            uploadPath.toFile().mkdirs(); // 如果資料夾不存在則創建
        }
        // 生成檔案名稱
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // File destination = new File(uploadPath, fileName);
        Path destination = uploadPath.resolve(fileName);
        // 儲存檔案
        file.transferTo(destination);
        // 返回檔案的 URL 路徑
        return "/uploads/" + employeeId + "/" + fileName;
    }
    // 新增或更新頭像的方法
    public void updateProfilePicture(Integer employeeId, String profilePicture) {
        employeeRepository.findById(employeeId).ifPresentOrElse(employee -> {
            // 若 profilePicture 不為 null，才更新頭像
            if (profilePicture != null) {
                employee.setProfilePicture(profilePicture);
                employeeRepository.save(employee);
            }
        }, () -> {
            throw new IllegalArgumentException("員工 ID: " + employeeId + " 的資料不存在");
        });
    }
    
}
