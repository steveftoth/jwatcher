package com.codekoan.csv;

import java.sql.JDBCType;

public class CSVColumnDef {
    final String csvName;
    final JDBCType sqlType;
    final String dbName;

    public CSVColumnDef(String csvName, JDBCType sqlType) {
        this.csvName = csvName;
        this.sqlType = sqlType;
        this.dbName = csvName.replaceAll(" ", "");
    }

    Object map(String value) {
        switch (sqlType) {
            case INTEGER:
                return Integer.valueOf(value);
            case FLOAT:
                return Float.valueOf(value);
            default:
            case VARCHAR:
                return value;
        }
    }

    public String getCsvName() {
        return csvName;
    }

    public String getDbName() {
        return dbName;
    }

}