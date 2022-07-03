package com.module.Application.repository;

import com.module.Application.models.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    /** Get all the transactions made in the second before now **/
    @Query(value="SELECT * FROM operation WHERE (origin_account =:accountId OR destination_account=:accountId) " +
            "AND transference_date > NOW() - INTERVAL 1 SECOND", nativeQuery = true)
    public List<Operation> operationsWithinOneSecond(@Param("accountId") Long accountId);

    /** Get the maximum quantity of the transactions for an account, divided by days **/
    @Query(value="SELECT MAX(t.sum) FROM (SELECT DATE(transference_date) " +
            "AS transaction_date, SUM(amount) AS sum FROM operation " +
            "WHERE origin_account = :accountId GROUP BY transaction_date) AS t", nativeQuery = true)
    public BigDecimal maxQuantityIn24HoursInHistory(@Param("accountId") Long accountId);

    /** Get total of transactions for an account in the last 24 hours **/
    @Query(value="SELECT SUM(amount) AS sum FROM operation WHERE origin_account = :accountId " +
            "AND transference_date >= NOW() - INTERVAL 1 DAY", nativeQuery = true)
    public BigDecimal totalLast24Hours(@Param("accountId") Long accountId);

}
