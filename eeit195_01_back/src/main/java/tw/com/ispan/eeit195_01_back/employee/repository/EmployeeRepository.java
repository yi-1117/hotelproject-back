package tw.com.ispan.eeit195_01_back.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeBean;

public interface EmployeeRepository extends JpaRepository<EmployeeBean, Integer>{
    Optional<EmployeeBean> findByEmail(String email);
    Optional<EmployeeBean> findByEmployeeId(Integer employeeId);
}
