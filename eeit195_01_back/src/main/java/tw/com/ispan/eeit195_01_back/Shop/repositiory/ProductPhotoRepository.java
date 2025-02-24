package tw.com.ispan.eeit195_01_back.shop.repositiory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductPhoto;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer> {
    List<ProductPhoto> findByProduct_ProductId(Integer productId); // 使用正確的命名慣例

}
