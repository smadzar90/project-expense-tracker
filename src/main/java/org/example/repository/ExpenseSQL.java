package org.example.repository;

class ExpenseSQL {
     static final String COLUMNS =
            """
            p.ID AS PROJECT_ID, p.NAME AS PROJECT_NAME, p.DESCRIPTION AS PROJECT_DESCRIPTION,
            p.START_DATE, p.BUDGET, p.COMPLETED, e.ID AS EXPENSE_ID, e.DESCRIPTION AS EXPENSE_DESCRIPTION,
            e.AMOUNT, e.TRANSACTION_DATE, c.ID AS C_ID, c.NAME AS C_NAME, c.DESCRIPTION AS C_DESCRIPTION,
            pm.ID AS PM_ID, pm.NAME AS PM_NAME, pm.DESCRIPTION AS PM_DESCRIPTION
            """;

     static final String SAVE_SQL = "INSERT INTO EXPENSE (PROJECT_ID, CATEGORY_ID, PAYMENT_METHOD_ID, DESCRIPTION, AMOUNT, TRANSACTION_DATE) VALUES(?, ?, ?, ?, ?, ?);";

     static final String FIND_ALL_SQL= "SELECT "
            .concat(COLUMNS)
            .concat("FROM EXPENSE e  LEFT JOIN PROJECT p ON e.PROJECT_ID = p.ID LEFT JOIN CATEGORY c ON e.CATEGORY_ID = c.ID LEFT JOIN PAYMENT_METHOD pm ON e.PAYMENT_METHOD_ID = pm.ID;");

     static final String FIND_BY_ID_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1) + " WHERE e.ID = ?;";

     static final String FIND_ALL_BY_ATTRIBUTE_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1)
            .concat( " WHERE %s = ?;");

     static final String FIND_ALL_BY_ATT_GREATER_THAN_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1)
            .concat( " WHERE %s > ?;");

     static final String FIND_ALL_BY_ATT_SMALLER_THAN_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1)
            .concat( " WHERE %s < ?;");

     static final String UPDATE_SQL = "UPDATE EXPENSE SET PROJECT_ID = ?, CATEGORY_ID = ?, PAYMENT_METHOD_ID = ?, DESCRIPTION = ?, AMOUNT = ?, TRANSACTION_DATE = ? WHERE ID = %d;";

     static final String DELETE_SQL = "DELETE FROM EXPENSE WHERE ID = ?;";
}
