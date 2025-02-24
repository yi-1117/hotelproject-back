// package tw.com.ispan.eeit195_01_back.shop.bean;

// import jakarta.persistence.Id;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;

// import jakarta.persistence.Table;

// @Entity
// @Table(name = "shopcustomer_info")

// public class ShopCustomerInfo {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private int id; // shop_customer_id
// private int memberId; // member_id
// private int shopperPhone;
// private String shopperFirstName;
// private String shopperLastName;
// private String shopperEmail;
// private String shopperPassword;
// private String shopperAddress;
// private boolean shopperGender;

// public int getId() {
// return id;
// }

// public void setId(int id) {
// this.id = id;
// }

// public int getMemberId() {
// return memberId;
// }

// public void setMemberId(int memberId) {
// this.memberId = memberId;
// }

// public int getShopperPhone() {
// return shopperPhone;
// }

// public void setShopperPhone(int shopperPhone) {
// this.shopperPhone = shopperPhone;
// }

// public String getShopperFirstName() {
// return shopperFirstName;
// }

// public void setShopperFirstName(String shopperFirstName) {
// this.shopperFirstName = shopperFirstName;
// }

// public String getShopperLastName() {
// return shopperLastName;
// }

// public void setShopperLastName(String shopperLastName) {
// this.shopperLastName = shopperLastName;
// }

// public String getShopperEmail() {
// return shopperEmail;
// }

// public void setShopperEmail(String shopperEmail) {
// this.shopperEmail = shopperEmail;
// }

// public String getShopperPassword() {
// return shopperPassword;
// }

// public void setShopperPassword(String shopperPassword) {
// this.shopperPassword = shopperPassword;
// }

// public String getShopperAddress() {
// return shopperAddress;
// }

// public void setShopperAddress(String shopperAddress) {
// this.shopperAddress = shopperAddress;
// }

// public boolean isShopperGender() {
// return shopperGender;
// }

// public void setShopperGender(boolean shopperGender) {
// this.shopperGender = shopperGender;
// }

// @Override
// public String toString() {
// return "ShopCustomerInfo [id=" + id + ", memberId=" + memberId + ",
// shopperPhone=" + shopperPhone
// + ", shopperFirstName=" + shopperFirstName + ", shopperLastName=" +
// shopperLastName + ", shopperEmail="
// + shopperEmail + ", shopperPassword=" + shopperPassword + ", shopperAddress="
// + shopperAddress
// + ", shopperGender=" + shopperGender + "]";
// }

// }
