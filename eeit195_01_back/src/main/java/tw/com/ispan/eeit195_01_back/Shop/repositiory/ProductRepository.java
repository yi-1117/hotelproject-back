package tw.com.ispan.eeit195_01_back.shop.repositiory;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.dao.ProductDAO;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductDAO {
    boolean existsByProductName(String productName);
}
