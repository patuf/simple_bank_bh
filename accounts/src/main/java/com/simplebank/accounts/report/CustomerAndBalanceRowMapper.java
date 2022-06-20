package com.simplebank.accounts.report;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

class CustomerAndBalanceRowMapper extends IndexKeepingRowMapper<Long, CustomerAndBalance> {

    @Override
    public CustomerAndBalance doMapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerAndBalance row = new CustomerAndBalance();
        row.setCustomerId(rs.getLong("CUSTOMER_ID"));
        row.setName(rs.getString("NAME"));
        row.setSurname(rs.getString("SURNAME"));
        row.setNumAccounts(rs.getInt("NUM_ACCS"));
        row.setBalance(0.);
        return row;
    }

    @Override
    Long getIndexValue(CustomerAndBalance row) {
        return row.getCustomerId();
    }
}
