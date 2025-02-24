package tw.com.ispan.eeit195_01_back.shop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductCategoryRepository;

@Service
@Transactional
public class ProductCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryService.class);
    @Autowired
    private ProductCategoryRepository productcategoryrepository;

    public List<ProductCategory> select(ProductCategory productcategory) {
        List<ProductCategory> result = null;
        if (productcategory != null && productcategory.getProductCategoryId() != null
                && !productcategory.getProductCategoryId().equals(0)) {
            Optional<ProductCategory> optional = productcategoryrepository
                    .findById(productcategory.getProductCategoryId());
            if (optional.isPresent()) {
                result = new ArrayList<ProductCategory>();
                result.add(optional.get());
            }
        } else {
            result = productcategoryrepository.findAll();
        }
        return result;
    }

    public ProductCategory insert(ProductCategory productCategory) {
        if (productCategory != null) {
            // 確認類別名稱不重複
            if (!productcategoryrepository.existsByCategoryName(productCategory.getCategoryName())) {
                return productcategoryrepository.save(productCategory); // 儲存並返回
            } else {
                throw new IllegalArgumentException("Category name already exists.");
            }
        }
        return null; // 若 productCategory 為 null，返回 null
    }

    public ProductCategory update(ProductCategory productcategory) {
        if (productcategory != null && productcategory.getProductCategoryId() != null) {
            logger.info("Attempting to update ProductCategory with ID: {}", productcategory.getProductCategoryId());

            if (productcategoryrepository.existsById(productcategory.getProductCategoryId())) {
                logger.info("ProductCategory found, proceeding with update.");
                return productcategoryrepository.save(productcategory);
            } else {
                logger.warn("ProductCategory with ID {} does not exist.", productcategory.getProductCategoryId());
            }
        } else {
            logger.warn("ProductCategory is null or ID is null.");
        }
        return null;
    }

    public boolean delete(ProductCategory productcategory) {
        if (productcategory != null && productcategory.getProductCategoryId() != null) {
            if (productcategoryrepository.existsById(productcategory.getProductCategoryId())) {
                productcategoryrepository.deleteById(productcategory.getProductCategoryId());
                return true;
            }
        }
        return false;
    }

    public ProductCategory findById(Integer id) {
        if (id != null) {
            Optional<ProductCategory> optional = productcategoryrepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public Optional<ProductCategory> findByProductCategory(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String categoryName = obj.isNull("categoryName") ? null : obj.getString("categoryName");

            if (categoryName != null && !categoryName.isEmpty()) {
                // 使用 repository 方法查詢
                return productcategoryrepository.findByCategoryName(categoryName);
            }
        } catch (JSONException e) {
            logger.error("Error parsing JSON: {}", e.getMessage());
        }
        return Optional.empty(); // 如果 categoryName 為 null 或發生異常，返回空的 Optional
    }

    public boolean exists(Integer id) {
        if (id != null) {
            return productcategoryrepository.existsById(id);
        }
        return false;
    }

    public ProductCategory create(String json) {
        System.err.println("Entering create method");
        try {
            JSONObject obj = new JSONObject(json);
            logger.info("Product Category JSON: {}", obj);

            String categoryName = obj.isNull("categoryName") ? null : obj.getString("categoryName");
            logger.info("Extracted categoryName: {}", categoryName);
            if (categoryName != null && !categoryName.isEmpty() &&
                    !productcategoryrepository.existsByCategoryName(categoryName)) {
                ProductCategory insert = new ProductCategory();
                // insert.setProductCategoryId(productCategoryId);
                insert.setCategoryName(categoryName);
                logger.info("Preparing to save ProductCategory with ID: {} and Name: {}",
                        categoryName);
                return productcategoryrepository.save(insert);
            }

        } catch (JSONException e) {
            logger.error("JSON parsing error occurred: {}", e.getMessage(), e);
            throw e; // 重新拋出例外，讓呼叫端處理

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            throw e; // 重新拋出例外，讓呼叫端處理

        } catch (Exception e) {
            logger.error("Unexpected error occurred during create: {}", e.getMessage(), e);
            throw e; // 重新拋出例外，讓呼叫端處理
        }
        return null; // 確保在其他情況下返回 null
    }

    public ProductCategory modify(String json) {
        try {
            // 將傳入的 JSON 字串轉換為 JSONObject 物件
            JSONObject obj = new JSONObject(json);

            // 從 JSON 中提取出對應的欄位
            Integer productCategoryId = obj.isNull("productCategoryId") ? null : obj.getInt("productCategoryId");
            String categoryName = obj.isNull("categoryName") ? null : obj.getString("categoryName");

            // 檢查 productCategoryId 是否有效
            if (productCategoryId != null) {
                // 根據 productCategoryId 查找對應的 ProductCategory
                Optional<ProductCategory> optional = productcategoryrepository.findById(productCategoryId);
                if (optional.isPresent()) {
                    ProductCategory update = optional.get();
                    // 根據 JSON 中的資料來更新欄位
                    update.setCategoryName(categoryName);

                    // 儲存並回傳更新後的 ProductCategory
                    return productcategoryrepository.save(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(Integer productCategoryId) {
        if (productCategoryId != null && productcategoryrepository.existsById(productCategoryId)) {
            productcategoryrepository.deleteById(productCategoryId);
            return true;
        }
        return false;
    }

    public long count(String json) {
        try {
            // 假設需要從 JSON 中解析條件並計算資料庫中的數量
            @SuppressWarnings("unused")
            JSONObject obj = new JSONObject(json);
            return productcategoryrepository.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<ProductCategory> find(String json) {
        try {
            // 假設從 JSON 中解析過濾條件，並從資料庫中查詢
            @SuppressWarnings("unused")
            JSONObject obj = new JSONObject(json);
            // 返回資料庫查詢結果，這裡假設可以根據某些條件查詢商品分類
            return productcategoryrepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
