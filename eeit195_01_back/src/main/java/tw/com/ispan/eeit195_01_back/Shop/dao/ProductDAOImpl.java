package tw.com.ispan.eeit195_01_back.shop.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tw.com.ispan.eeit195_01_back.shop.bean.Product;
// import tw.com.ispan.util.DatetimeConverter;

@Repository
public class ProductDAOImpl implements ProductDAO {
	@PersistenceContext
	private EntityManager entityManager = null;

	public EntityManager getSession() {
		return entityManager;
	}

	@Override
	public Long count(JSONObject param) {
		// select count(*) from product where ...
		Integer id = param.isNull("id") ? null : param.getInt("id");
		String name = param.isNull("name") ? null : param.getString("name");
		Double pricemin = param.isNull("pricemin") ? null : param.getDouble("pricemin");
		Double pricemax = param.isNull("pricemax") ? null : param.getDouble("pricemax");
		// String makemin = param.isNull("makemin") ? null : param.getString("makemin");
		// String makemax = param.isNull("makemax") ? null : param.getString("makemax");
		Integer expiremin = param.isNull("expiremin") ? null : param.getInt("expiremin");
		Integer expiremax = param.isNull("expiremax") ? null : param.getInt("expiremax");

		CriteriaBuilder criteriaBuilder = this.getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Product> table = criteriaQuery.from(Product.class);

		// select
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(table));

		// where
		List<Predicate> predicates = new ArrayList<>();

		if (id != null) {
			predicates.add(
					criteriaBuilder.equal(table.get("id"), id));
		}

		if (name != null && name.length() != 0) {
			predicates.add(
					criteriaBuilder.like(table.get("name"), "%" + name + "%"));
		}

		if (pricemin != null) {
			predicates.add(
					criteriaBuilder.greaterThan(table.get("price"), pricemin));
		}
		if (pricemax != null) {
			predicates.add(
					criteriaBuilder.lessThan(table.get("price"), pricemax));
		}

		// if (makemin != null) {
		// java.util.Date date = DatetimeConverter.parse(makemin, "yyyy-MM-dd");
		// predicates.add(
		// criteriaBuilder.greaterThan(table.get("make"), date));
		// }
		// if (makemax != null) {
		// java.util.Date date = DatetimeConverter.parse(makemax, "yyyy-MM-dd");
		// predicates.add(
		// criteriaBuilder.lessThan(table.get("expire"), date));
		// }

		if (expiremin != null) {
			predicates.add(
					criteriaBuilder.greaterThan(table.get("expire"), expiremin));
		}
		if (expiremax != null) {
			predicates.add(
					criteriaBuilder.lessThan(table.get("expire"), expiremax));
		}

		if (!predicates.isEmpty()) {
			criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		TypedQuery<Long> typedQuery = this.getSession().createQuery(criteriaQuery);
		Long result = typedQuery.getSingleResult();
		if (result != null) {
			return result;
		} else {
			return 0L;
		}
	}

	@Override
	public List<Product> find(JSONObject param) {
		System.out.println("param=" + param);

		Integer id = param.isNull("productId") ? null : param.getInt("productId");
		String name = param.isNull("productName") ? null : param.getString("productName");
		BigDecimal pricemin = param.isNull("pricemin") ? null : BigDecimal.valueOf(param.getDouble("pricemin"));
		BigDecimal pricemax = param.isNull("pricemax") ? null : BigDecimal.valueOf(param.getDouble("pricemax"));
		String brandName = param.isNull("brandName") ? null : param.getString("brandName");
		Integer capacity = param.isNull("capacity") ? null : param.getInt("capacity");
		Integer stockQuantityMin = param.isNull("stockQuantityMin") ? null : param.getInt("stockQuantityMin");
		Integer stockQuantityMax = param.isNull("stockQuantityMax") ? null : param.getInt("stockQuantityMax");

		LocalDateTime createdDateMin = param.isNull("createdDateMin") ? null
				: LocalDateTime.parse(param.getString("createdDateMin"));
		LocalDateTime createdDateMax = param.isNull("createdDateMax") ? null
				: LocalDateTime.parse(param.getString("createdDateMax"));

		Integer start = param.isNull("start") ? null : param.getInt("start");
		Integer rows = param.isNull("rows") ? 5 : param.getInt("rows");
		String sort = param.isNull("sort") ? null : param.getString("sort");
		boolean dir = param.isNull("dir") ? false : param.getBoolean("dir");

		CriteriaBuilder criteriaBuilder = this.getSession().getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> table = criteriaQuery.from(Product.class);

		// where
		List<Predicate> predicates = new ArrayList<>();

		if (id != null) {
			predicates.add(criteriaBuilder.equal(table.get("productId"), id));
		}

		if (name != null && !name.isEmpty()) {
			predicates.add(criteriaBuilder.like(table.get("productName"), "%" + name + "%"));
		}

		if (pricemin != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(table.get("productUnitPrice"), pricemin));
		}
		if (pricemax != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(table.get("productUnitPrice"), pricemax));
		}

		if (brandName != null && !brandName.isEmpty()) {
			predicates.add(criteriaBuilder.like(table.get("brandName"), "%" + brandName + "%"));
		}

		if (capacity != null) {
			predicates.add(criteriaBuilder.equal(table.get("capacity"), capacity));
		}

		if (stockQuantityMin != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(table.get("stockQuantity"), stockQuantityMin));
		}
		if (stockQuantityMax != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(table.get("stockQuantity"), stockQuantityMax));
		}

		if (createdDateMin != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(table.get("createdDate"), createdDateMin));
		}
		if (createdDateMax != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(table.get("createdDate"), createdDateMax));
		}

		if (!predicates.isEmpty()) {
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		// order by
		if (sort != null && !sort.isEmpty()) {
			if (dir) {
				criteriaQuery.orderBy(criteriaBuilder.desc(table.get(sort)));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.asc(table.get(sort)));
			}
		}

		// 分頁處理
		List<Product> result = this.getSession().createQuery(criteriaQuery)
				.setFirstResult(start == null ? 0 : start)
				.setMaxResults(rows)
				.getResultList();

		return result;
	}

}
