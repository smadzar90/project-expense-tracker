package org.example.repository;

import org.example.model.PaymentMethod;
import org.example.repository.base.EntityRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRepository extends EntityRepository<PaymentMethod> {
    private final String FIND_ALL_SQL = "SELECT ID AS PM_ID, NAME AS PM_NAME, DESCRIPTION AS PM_DESCRIPTION FROM PAYMENT_METHOD;";
    private final String FIND_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1).concat(" WHERE ID=?;");

    public PaymentRepository(Connection connection) {
        super(connection);
    }

    @Override
    public PaymentMethod mapResultSetToEntity(ResultSet rs) {
        try {
            PaymentMethod payment = new PaymentMethod(rs.getString("PM_NAME"), rs.getString("PM_DESCRIPTION"));
            payment.setId(rs.getLong("PM_ID"));
            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the result set to Payment entity. " + e.getMessage());
        }
    }

    @Override
    protected String getFindSQL() {
        return FIND_SQL;
    }

    @Override
    protected String getFindAllSQL() {
        return FIND_ALL_SQL;
    }
}
