package tw.com.ispan.eeit195_01_back.shop.bean;
//package tw.com.ispan.eeit195_01_back.shop.bean;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import jakarta.persistence.Id;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "shoppingpromocode")
//
//public class ShoppingPromoCode {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id; // ShoppingPromoCode_ID
//    private String code; // ShoppingPromoCode
//    private LocalDateTime date; // ShoppingPromoCode_Date
//    private int limit; // ShoppingPromoCode_limit
//    private float discountPercent; // room_promo_perent
//    private BigDecimal amount; // room_promo_amount, shop_promo_min_money
//    private int levelLimit; // level_limit
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public LocalDateTime getDate() {
//        return date;
//    }
//
//    public void setDate(LocalDateTime date) {
//        this.date = date;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public void setLimit(int limit) {
//        this.limit = limit;
//    }
//
//    public float getPercent() {
//        return percent;
//    }
//
//    public void setPercent(float percent) {
//        this.percent = percent;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public int getLevelLimit() {
//        return levelLimit;
//    }
//
//    public void setLevelLimit(int levelLimit) {
//        this.levelLimit = levelLimit;
//    }
//
//    @Override
//    public String toString() {
//        return "ShoppingPromoCode [id=" + id + ", code=" + code + ", date=" + date + ", limit=" + limit + ", percent="
//                + percent + ", amount=" + amount + ", levelLimit=" + levelLimit + "]";
//    }
//
//}
