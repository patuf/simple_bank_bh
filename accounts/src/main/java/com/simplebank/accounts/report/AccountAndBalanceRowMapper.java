package com.simplebank.accounts.report;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

class AccountAndBalanceRowMapper extends IndexKeepingRowMapper<Long, AccountAndBalance> {

    @Override
    AccountAndBalance doMapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }

    @Override
    Long getIndexValue(AccountAndBalance row) {
        return row.getId();
    }
}
