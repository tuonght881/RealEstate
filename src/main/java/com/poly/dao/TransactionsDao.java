package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.poly.bean.Transactions;

public interface TransactionsDao extends JpaRepository<Transactions, Integer> {

	@Query("Select o from Transactions o where o.users.username = ?1")
	public Transactions getTransaction(String user_id);

	@Query(value = "Select top 1 * from Transactions where users = ?1 ORDER BY transactions_id DESC", nativeQuery = true)
	public Transactions getTransactionByUserId(String user_id);

	@Query(value = "Select top 1 * from Transactions where transactions_id = ?1", nativeQuery = true)
	public Transactions getTransactionById(Integer user_id);

	@Query("SELECT MONTH(t.create_at) AS month, YEAR(t.create_at) AS year, SUM(dt.price) AS total_price "
			+ "FROM Transactions t INNER JOIN DetailTransactions dt ON t.transactions_id = dt.transactions_id "
			+ "WHERE YEAR(t.create_at) = :year and dt.transactions_type = 'true'" + "GROUP BY YEAR(t.create_at), MONTH(t.create_at) "
			+ "ORDER BY YEAR(t.create_at), MONTH(t.create_at)")
	public List<Object[]> getMonthlyTotalPrices(@Param("year") int year);
	
	@Query(value= "SELECT MONTH(t.create_at) AS month, YEAR(t.create_at) AS year, SUM(dt.price) AS total_price FROM transactions t INNER JOIN "
			+ "detail_transactions dt ON t.transactions_id = dt.transactions_id "
			+ "WHERE MONTH(t.create_at) = MONTH(GETDATE()) and YEAR(t.create_at) = YEAR(GETDATE()) and dt.transactions_type = 1 "
			+ "GROUP BY YEAR(t.create_at), MONTH(t.create_at) "
			+ "ORDER BY YEAR(t.create_at), MONTH(t.create_at);", nativeQuery = true)
	public List<Object[]> getCurrentAndPreviousMonth();
	
	@Query(value= "SELECT YEAR(t.create_at) AS years, SUM(dt.price) AS total_price FROM transactions t INNER JOIN "
			+ "detail_transactions dt ON t.transactions_id = dt.transactions_id "
			+ "WHERE YEAR(t.create_at) IN (SELECT DISTINCT TOP 3 YEAR(create_at) FROM transactions ORDER BY YEAR(create_at) DESC) and dt.transactions_type = 1 "
			+ "GROUP BY YEAR(t.create_at) ORDER BY years DESC", nativeQuery = true)
	public List<Object[]> getIncomeInRecentYears();
	
	@Query(value= "SELECT YEAR(t.create_at) AS year "
			+ "FROM Transactions t INNER JOIN detail_transactions dt ON t.transactions_id = dt.transactions_id "
			+ "WHERE dt.transactions_type = 1 GROUP BY YEAR(t.create_at) "
			+ "ORDER BY YEAR(t.create_at) DESC", nativeQuery = true)
	public List<Object[]> getYears();
	
	@Query(value="SELECT SUM(dt.price) AS total_price FROM transactions t INNER JOIN detail_transactions dt ON t.transactions_id = dt.transactions_id WHERE YEAR(t.create_at) = ?1 and dt.transactions_type = 1", nativeQuery = true)
	public Double getTotalYear(int year);

}
