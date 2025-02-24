package tw.com.ispan.eeit195_01_back.shop.dao;

import java.util.List;

import org.json.JSONObject;

import tw.com.ispan.eeit195_01_back.shop.bean.Product;

public interface ProductDAO {
	public abstract Long count(JSONObject param);

	public abstract List<Product> find(JSONObject param);
}