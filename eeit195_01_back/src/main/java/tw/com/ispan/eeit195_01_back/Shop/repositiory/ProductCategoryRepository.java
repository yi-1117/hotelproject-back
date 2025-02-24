package tw.com.ispan.eeit195_01_back.shop.repositiory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    boolean existsByCategoryName(String categoryName);

    Optional<ProductCategory> findByCategoryName(String categoryName);
}
