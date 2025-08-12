package com.transport.tracking.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${db.schema}")
    private String dbSchema;

    public List<Map<String, Object>> getDepotCustomer() {
        String sql = "SELECT " +
                "    c.BPCNUM_0     AS id, " +
                "    c.BPCNAM_0     AS value, " +
                "    c.BCGCOD_0     AS customer_category, " +
                "    a.CTY_0        AS city, " +
                "    a.XX10C_GEOX_0 AS lng, " +
                "    a.XX10C_GEOY_0 AS lat, " +
                "    a.BPAADD_0     AS docnum " +
                "FROM " + dbSchema + ".BPCUSTOMER c " +
                "JOIN " + dbSchema + ".BPADDRESS a " +
                "    ON c.BPCNUM_0 = a.BPANUM_0 " +
                "WHERE c.BCGCOD_0 = ?";
        return jdbcTemplate.queryForList(sql,"DEPOT");
    }
}
