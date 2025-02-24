package tw.com.ispan.eeit195_01_back.shop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductCategoryRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductRepository;

@Service
@Transactional
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryService.class);
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<Product> select(Product product) {
        List<Product> result = null;
        if (product != null && product.getProductId() != null && !product.getProductId().equals(0)) {
            Optional<Product> optional = productRepository.findById(product.getProductId());
            if (optional.isPresent()) {
                result = new ArrayList<Product>();
                result.add(optional.get());
            }
        } else {
            result = productRepository.findAll();
        }
        return result;
    }

    public Product insert(Product product) {
        if (product != null) {
            // 如果 productId 為 null，則直接保存，讓資料庫生成 ID
            if (product.getProductId() == null || !productRepository.existsById(product.getProductId())) {
                return productRepository.save(product); // 儲存新產品
            }
        }
        return null; // 如果不符合條件則返回 null
    }

    public Product update(Product product) {
        if (product != null && product.getProductId() != null) {
            if (productRepository.existsById(product.getProductId())) {
                return productRepository.save(product);
            }
        }
        return null;
    }

    public boolean delete(Product product) {
        if (product != null && product.getProductId() != null) {
            if (productRepository.existsById(product.getProductId())) {
                productRepository.deleteById(product.getProductId());
                return true;
            }
        }
        return false;
    }

    public Product findById(Integer id) {
        if (id != null) {
            Optional<Product> optional = productRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public boolean exists(Integer id) {
        if (id != null) {
            return productRepository.existsById(id);
        }
        return false;
    }

    public Product create(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // 提取 JSON 中的各個屬性
            String productName = obj.isNull("productName") ? null : obj.getString("productName");
            String sku = obj.isNull("sku") ? null : obj.getString("sku");
            String brandName = obj.isNull("brandName") ? null : obj.getString("brandName");
            BigDecimal productUnitPrice = obj.isNull("productUnitPrice") ? null : obj.getBigDecimal("productUnitPrice");
            BigDecimal discount = obj.isNull("discount") ? null : obj.getBigDecimal("discount");
            String productDescription = obj.isNull("productDescription") ? null : obj.getString("productDescription");
            Integer capacity = obj.isNull("capacity") ? null : obj.getInt("capacity");
            Integer stockQuantity = obj.isNull("stockQuantity") ? null : obj.getInt("stockQuantity");
            String sellerName = obj.isNull("sellerName") ? null : obj.getString("sellerName");
            String sellerPhone = obj.isNull("sellerPhone") ? null : obj.getString("sellerPhone");
            String sellerEmail = obj.isNull("sellerEmail") ? null : obj.getString("sellerEmail");
            String sellerAddress = obj.isNull("sellerAddress") ? null : obj.getString("sellerAddress");
            Integer productCategoryId = obj.isNull("productCategoryId") ? null : obj.getInt("productCategoryId");
            logger.info("還沒塞");
            boolean productExists = productRepository.existsByProductName(productName);
            boolean categoryIdNotNull = productCategoryId != null;
            boolean categoryExists = productCategoryRepository.existsById(productCategoryId);

            logger.info("Product exists: {}", productExists);
            logger.info("ProductCategory ID is not null: {}", categoryIdNotNull);
            logger.info("ProductCategory exists: {}", categoryExists);
            // 檢查商品名稱是否已存在和商品類別是否有效
            if (!productRepository.existsByProductName(productName) && productCategoryId != null &&
                    productCategoryRepository.existsById(productCategoryId)) {
                logger.info("進行塞資料");
                // 創建新的 Product 實體
                Product product = new Product();
                product.setProductName(productName);
                product.setSku(sku);
                product.setBrandName(brandName);
                product.setProductUnitPrice(productUnitPrice);
                product.setDiscount(discount);
                product.setProductDescription(productDescription);
                product.setCapacity(capacity);
                product.setStockQuantity(stockQuantity);
                product.setSellerName(sellerName);
                product.setSellerPhone(sellerPhone);
                product.setSellerEmail(sellerEmail);
                product.setSellerAddress(sellerAddress);
                product.setCreatedDate(LocalDateTime.now());
                product.setLastUpdatedDate(LocalDateTime.now());
                logger.info("456" + product.toString());
                // 根據 productCategoryId 查找對應的 ProductCategory，並將其設置為外鍵
                Optional<ProductCategory> category = productCategoryRepository.findById(productCategoryId);
                if (category.isPresent()) {
                    product.setProductCategory(category.get()); // 設置外鍵
                } else {
                    throw new IllegalArgumentException("ProductCategory with ID " + productCategoryId + " not found.");
                }
                logger.info("789" + product.toString());
                // 保存並返回 Product 實體
                return productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Invalid product name or category.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 若發生錯誤或 JSON 不完整，返回 null
    }

    public Product modify(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer id = obj.isNull("productId") ? null : obj.getInt("productId");

            // 根據 ID 查找產品
            if (id != null) {
                Optional<Product> optional = productRepository.findById(id);
                if (optional.isPresent()) {
                    Product existingProduct = optional.get();

                    // 更新存在的產品屬性
                    if (!obj.isNull("productName"))
                        existingProduct.setProductName(obj.getString("productName"));
                    if (!obj.isNull("sku"))
                        existingProduct.setSku(obj.getString("sku"));
                    if (!obj.isNull("brandName"))
                        existingProduct.setBrandName(obj.getString("brandName"));
                    if (!obj.isNull("productUnitPrice"))
                        existingProduct.setProductUnitPrice(obj.getBigDecimal("productUnitPrice"));
                    if (!obj.isNull("discount"))
                        existingProduct.setDiscount(obj.getBigDecimal("discount"));
                    if (!obj.isNull("productDescription"))
                        existingProduct.setProductDescription(obj.getString("productDescription"));
                    if (!obj.isNull("capacity"))
                        existingProduct.setCapacity(obj.getInt("capacity"));
                    if (!obj.isNull("stockQuantity"))
                        existingProduct.setStockQuantity(obj.getInt("stockQuantity"));
                    if (!obj.isNull("sellerName"))
                        existingProduct.setSellerName(obj.getString("sellerName"));
                    if (!obj.isNull("sellerPhone"))
                        existingProduct.setSellerPhone(obj.getString("sellerPhone"));
                    if (!obj.isNull("sellerEmail"))
                        existingProduct.setSellerEmail(obj.getString("sellerEmail"));
                    if (!obj.isNull("sellerAddress"))
                        existingProduct.setSellerAddress(obj.getString("sellerAddress"));

                    existingProduct.setLastUpdatedDate(LocalDateTime.now());
                    // 保存並返回更新後的產品
                    return productRepository.save(existingProduct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 若發生錯誤或 JSON 不完整，返回 null
    }

    public boolean remove(Integer id) {
        if (id != null && productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // public long count(String json) {
    // try {
    // JSONObject obj = new JSONObject(json);
    // return productRepository.count(obj);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return 0;
    // }

    public List<Product> find(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return productRepository.find(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
