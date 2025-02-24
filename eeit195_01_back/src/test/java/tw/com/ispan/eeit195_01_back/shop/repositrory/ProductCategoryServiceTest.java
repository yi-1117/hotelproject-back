package tw.com.ispan.eeit195_01_back.shop.repositrory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductCategoryRepository;
import tw.com.ispan.eeit195_01_back.shop.service.ProductCategoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@Transactional
public class ProductCategoryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryServiceTest.class);

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    // ProductCategory productCategory = new ProductCategory();
    private ProductCategory productCategory;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.builder()
                // .productCategoryId(1)
                .categoryName("Electronics")
                .build();
        productCategoryRepository.save(productCategory);

        // productCategory.setProductCategoryId(2);
        // productCategory.setCategoryName("Electronics");
        // shoppingCart.setShoppingCartId(1);
    }

    // 測試 insert 方法
    @Test
    void testInsert() {
        // Act: 呼叫 insert 方法
        ProductCategory result = productCategoryService.insert(productCategory);
        // Assert: 驗證 insert 行為
        assertNotNull(result);
        assertEquals(1, result.getProductCategoryId());
        assertEquals("Electronics", result.getCategoryName());
        logger.info("Product Category: {}", productCategory);
        logger.info("Shopping Cart: {}", shoppingCart);
        logger.info("成功!!!!!");
    }

    // 測試 update 方法
    @Test
    void testUpdate() {
        // 更新操作
        productCategory.setCategoryName("Updated pig");
        logger.info("Product Category: {}", productCategory);

        // Act: 呼叫 update 方法
        ProductCategory result = productCategoryService.update(productCategory);
        logger.info("456");

        // Assert: 驗證 update 行為
        assertNotNull(result); // 檢查結果是否為 null
        assertEquals(1, result.getProductCategoryId()); // 檢查 ID 是否正確
        assertEquals("Updated pig", result.getCategoryName()); // 檢查名稱是否已更新
    }

    // 測試 delete 方法
    @Test
    void testDelete() {
        // Act: 呼叫 delete 方法
        boolean result = productCategoryService.delete(productCategory);

        // Assert: 驗證 delete 行為
        assertTrue(result);
        verify(productCategoryRepository).deleteById(1); // 驗證 deleteById 是否被呼叫
    }

    // 測試 create 方法
@Test
void testCreate() {
// Arrange: 模擬 repository 的 existsById 方法
String json = "{\"productCategoryId\": 1, \"categoryName\":
\"Electronics\"}";
when(productCategoryRepository.existsById(1)).thenReturn(false);
when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

// Act: 呼叫 create 方法
ProductCategory result = productCategoryService.create(json);

// Assert: 驗證 create 方法行為
assertNotNull(result);
assertEquals(1, result.getProductCategoryId());
assertEquals("Electronics", result.getCategoryName());
}

    // 測試 modify 方法
@Test
void testModify() {
// Arrange: 模擬 repository 的 findById 和 save 方法
String json = "{\"productCategoryId\": 1, \"categoryName\": \"Updated
Electronics\"}";
when(productCategoryRepository.findById(1)).thenReturn(Optional.of(productCategory));
when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

// Act: 呼叫 modify 方法
ProductCategory result = productCategoryService.modify(json);

// Assert: 驗證 modify 方法行為
assertNotNull(result);
assertEquals(1, result.getProductCategoryId());
assertEquals("Updated Electronics", result.getCategoryName());
}

    // 測試 create 方法當 ID 已存在時
@Test
void testCreateWithExistingId() {
// Arrange: 模擬 repository 的 existsById 方法返回 true，表示 ID 已經存在
String json = "{\"productCategoryId\": 1, \"categoryName\":
\"Electronics\"}";
when(productCategoryRepository.existsById(1)).thenReturn(true);

// Act: 呼叫 create 方法
ProductCategory result = productCategoryService.create(json);

// Assert: 應該返回 null，因為 ID 已經存在
assertNull(result);
}

}
