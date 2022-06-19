package com.simplebank.accounts.report;

import com.simplebank.accounts.acc.AccountStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;

class AccountAndBalanceRowMapper extends IndexKeepingRowMapper<Long, AccountAndBalance> {

    @Override
    AccountAndBalance doMapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountAndBalance res = new AccountAndBalance();
        res.setId(rs.getLong("ID"));
        res.setCustomerId(rs.getLong("CUSTOMER_ID"));
        res.setAccountStatus(AccountStatus.values()[rs.getInt("ACCOUNT_STATUS")]);
        res.setTimeCreated(rs.getTimestamp("TIME_CREATED").toLocalDateTime());
        return res;
    }

    @Override
    Long getIndexValue(AccountAndBalance row) {
        return row.getId();
    }
}
