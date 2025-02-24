package tw.com.ispan.eeit195_01_back.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.employee.bean.EPasswordResetBean;
import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeBean;

public interface EPasswordResetRepository extends JpaRepository<EPasswordResetBean, Integer> {
    // 根據員工ID和是否已使用來查詢重設請求

    Optional<EPasswordResetBean> findByEmployeeEmployeeIdAndIsUsedFalse(Integer employeeId);

    Optional<EPasswordResetBean> findByEmployeeAndIsUsedFalse(EmployeeBean Employee);

    // 根據重設 Token 查詢
    Optional<EPasswordResetBean> findByResetToken(String resetToken);

    @Transactional
    void deleteByEmployeeEmployeeId(Integer employeeId);

}
