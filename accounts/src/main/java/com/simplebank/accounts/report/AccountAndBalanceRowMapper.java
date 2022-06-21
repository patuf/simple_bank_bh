package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.AccountStatus;
import com.simplebank.accounts.report.model.AccountAndBalance;

import java.sql.ResultSet;
import java.sql.SQLException;

class AccountAndBalanceRowMapper extends IndexKeepingRowMapper<Long, AccountAndBalance> {

    @Override
    AccountAndBalance doMapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountAndBalance row = new AccountAndBalance();
        row.setAccountId(rs.getLong("ACCOUNT_ID"));
        row.setCustomerId(rs.getLong("CUSTOMER_ID"));
        row.setAccountStatus(AccountStatus.values()[rs.getInt("ACCOUNT_STATUS")]);
        row.setTimeCreated(rs.getTimestamp("TIME_CREATED").toLocalDateTime());
        row.setBalance(0.);
        return row;
    }

    @Override
    Long getIndexValue(AccountAndBalance row) {
        return row.getAccountId();
    }
}
