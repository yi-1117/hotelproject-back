package tw.com.ispan.eeit195_01_back.shop.dto;

import java.util.List;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory;

public class ProductCategoryResponse {
    private Boolean success;
    private String message;
    private List<ProductCategory> list;
    private Long count;

    @Override
    public String toString() {
        return "ProductResponse [success=" + success + ", message=" + message + ", list=" + list + ", count=" + count
                + "]";
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProductCategory> getList() {
        return list;
    }

    public void setList(List<ProductCategory> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
