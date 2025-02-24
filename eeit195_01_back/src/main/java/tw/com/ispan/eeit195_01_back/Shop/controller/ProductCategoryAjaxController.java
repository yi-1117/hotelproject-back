package tw.com.ispan.eeit195_01_back.shop.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory; // 假設有 ProductCategoryBean 類別
import tw.com.ispan.eeit195_01_back.shop.dto.ProductCategoryResponse;
import tw.com.ispan.eeit195_01_back.shop.service.ProductCategoryService; // 假設有 ProductCategoryService 類別

@RestController
@RequestMapping("/ajax/pages/product-categories")
public class ProductCategoryAjaxController {
    @Autowired
    private ProductCategoryService productCategoryService; // 改為注入 ProductCategoryService

    @PostMapping
    public ProductCategoryResponse create(@RequestBody String json) {
        ProductCategoryResponse responseBean = new ProductCategoryResponse();

        // 修改 JSON 欄位名稱為 "productCategoryId"
        JSONObject obj = new JSONObject(json);
        Integer productCategoryId = obj.isNull("productCategoryId") ? null : obj.getInt("productCategoryId");

        if (productCategoryId == null) {
            responseBean.setSuccess(false);
            responseBean.setMessage("productCategoryId是必要欄位(bean)");
        } else if (productCategoryService.exists(productCategoryId)) {
            responseBean.setSuccess(false);
            responseBean.setMessage("productCategoryId已存在(bean)");
        } else {
            // 創建 ProductCategory 並設置屬性
            ProductCategory insert = productCategoryService.create(json);
            if (insert == null) {
                responseBean.setSuccess(false);
                responseBean.setMessage("新增失敗(bean)");
                System.out.println("Received JSON: " + json);
            } else {
                responseBean.setSuccess(true);
                responseBean.setMessage("新增成功(bean)");
            }
        }
        return responseBean;
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Integer id, @RequestBody String entity) {
        JSONObject responseJson = new JSONObject();
        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "Id是必要欄位");
        } else if (!productCategoryService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "Id不存在");
        } else {
            ProductCategory productCategory = productCategoryService.modify(entity);
            if (productCategory == null) {
                responseJson.put("success", false);
                responseJson.put("message", "修改失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "修改成功");
            }
        }
        return responseJson.toString();
    }

    @DeleteMapping("/{pk}")
    public String remove(@PathVariable("pk") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "id是必要欄位");
        } else if (!productCategoryService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "id不存在");
        } else {
            boolean delete = productCategoryService.remove(id);
            if (!delete) {
                responseJson.put("success", false);
                responseJson.put("message", "刪除失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "刪除成功");
            }
        }

        return responseJson.toString();
    }

    @GetMapping("/{id}")
    public String findByPrimaryKey(@PathVariable(name = "id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();
        if (id != null) {
            ProductCategory productCategory = productCategoryService.findById(id);
            if (productCategory != null) {
                JSONObject item = new JSONObject()
                        .put("id", productCategory.getProductCategoryId())
                        .put("categoryName", productCategory.getCategoryName()); // 假設有 categoryName
                array = array.put(item);
            }
        }
        responseJson = responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/find")
    public ProductCategoryResponse find(@RequestBody String json) {
        ProductCategoryResponse responseBean = new ProductCategoryResponse();

        long count = productCategoryService.count(json);
        responseBean.setCount(count);

        List<ProductCategory> productCategories = productCategoryService.find(json);
        if (productCategories != null && !productCategories.isEmpty()) {
            responseBean.setList(productCategories);
        } else {
            responseBean.setList(new ArrayList<>());
        }

        return responseBean;
    }
}
