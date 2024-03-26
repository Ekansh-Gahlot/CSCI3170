import java.sql.ResultSet;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A class that provides basic CRUD operations for any table in the connected database, can be manually configured to support multiple tables.
 */
public class BaseTableHandler {
    public String tableName;
    public String[] columns;
    public String[] primaryKeys;

    public BaseTableHandler(String tableName, String[] columns, String[] primaryKeys) {
        this.tableName = tableName;
        this.columns = columns;
        this.primaryKeys = primaryKeys;
    }

    /**
     * Combines strings with a separator
     * @param strings
     * @param separator
     * @return
     */
    private static String combineStrings(String[] strings, String separator) {
        return String.join(separator, strings);
    }

    /**
     * Returns a string that generates a string condition that equates the primary keys with placeholders for parameters
     * @return
     */
    private String getKeyEquatingCondition() {
        return combineStrings(Arrays.stream(primaryKeys).map(key -> key + " = ?").toArray(size -> new String[size]),
                " AND ");
    }

    /**
     * SELECT fields FROM table WHERE condition ORDER BY orderBy
     * This allows not only specific columns to be extracted, but also allows aggregated values to be extracted, e.g. max(column)
     * @param condition
     * @param fields
     * @param parameters
     * @param orderBy
     * @return
     */
    public ResultSet selectRecords(String condition, String[] fields, String[] parameters, String orderBy) {
        String statement = "SELECT ";
        statement += combineStrings(fields, ", ");
        statement += " FROM " + tableName;
        if (condition != null) {
            statement += " WHERE " + condition;
        }
        if (orderBy != null) {
            statement += " ORDER BY " + orderBy;
        }
        return DatabaseManager.executeStatement(statement, parameters);
    }

    /**
     * SELECT * FROM table WHERE condition ORDER BY orderBy
     * @param condition
     * @param parameters
     * @param orderBy
     * @return
     */
    public ResultSet selectRecords(String condition, String[] parameters, String orderBy) {
        return selectRecords(condition, new String[] { "*" }, parameters, orderBy);
    }

    /**
     * basically SELECT * FROM table WHERE condition
     * @param condition
     * @param parameters
     * @return
     */
    public ResultSet selectRecords(String condition, String[] parameters) {
        return selectRecords(condition, parameters, null);
    }

    /**
     * SELECT * FROM table WHERE (primary_key = keys)
     * @param keys
     * @return 
     */
    public ResultSet selectRecordByKey(String[] keys) {
        return selectRecords(getKeyEquatingCondition(), keys);
    }

    /**
     * INSERT INTO table (specificColumns) VALUES valuesString, where values are the parameters to be inserted
     * @param specificColumns the columns to insert into, if any
     * @param valuesString should contains placeholders for the values if there are values to be inserted
     * @param values the actual parameters to be inserted into the placeholders in valuesString
     * @return
     */
    public Boolean insertRecord(String[] specificColumns, String valuesString, String... values) {
        String statement = "INSERT INTO " + tableName;
        if (specificColumns.length > 0) {
            statement += " ( " + combineStrings(specificColumns, ", ") + " ) ";
        }
        statement += " VALUES " + valuesString;
        return DatabaseManager.executeStatement(statement, values) != null;
    }

    /**
     * INSERT INTO table (specificColumns) VALUES values, where values are the parameters to be inserted
     * @param specificColumns
     * @param values
     * @return
     */
    public Boolean insertRecord(String[] specificColumns, String... values) {
        String valuesString = " (" + combineStrings(
            Arrays.stream(specificColumns).map(column -> "?").toArray(size -> new String[size]), ", ") + ")" ;
        return insertRecord(specificColumns, valuesString, values);
    }

    /**
     * INSERT INTO table VALUES valuesString, where values are the parameters to be inserted into valuesString
     * @param valuesString
     * @param values
     * @return
     */
    public Boolean insertRecord(String valuesString, String[] values) {
        return insertRecord(columns, valuesString, values);
    }

    /**
     * INSERT INTO table VALUES values, where values are the parameters to be inserted
     * @param values
     * @return
     */
    public Boolean insertRecord(String... values) {
        return insertRecord(columns, values);
    }

    /**
     * UPDATE table SET (specificColumns = newValues) WHERE condition, where condition is a string with placeholders for conditionParameters
     * @param specificColumns
     * @param newValues
     * @param condition
     * @param conditionParameters
     * @return
     */
    public Boolean updateRecord(String[] specificColumns, String[] newValues, String condition,
            String... conditionParameters) {
        String statement = "UPDATE " + tableName + " SET ";
        statement += combineStrings(
                Arrays.stream(specificColumns).map(column -> column + " = ?").toArray(size -> new String[size]), ", ");
        statement += " WHERE " + condition;
        String[] allParameters = Stream.concat(Arrays.stream(newValues), Arrays.stream(conditionParameters))
                .toArray(String[]::new);
        return DatabaseManager.executeStatement(statement, allParameters) != null;
    }

    /**
     * UPDATE table SET (specificColumns = newValues) WHERE (primary_key = keys)
     * @param specificColumns
     * @param newValues
     * @param keys
     * @return
     */
    public Boolean updateRecordByKey(String[] specificColumns, String[] newValues, String[] keys) {
        return updateRecord(specificColumns, newValues, getKeyEquatingCondition(),
                keys);
    }
    
    /**
     * DELETE FROM table WHERE condition, where condition is a string with placeholders for parameters
     * @param condition
     * @param parameters
     * @return
     */
    public Boolean deleteRecord(String condition, String... parameters) {
        String statement = "DELETE FROM " + tableName + " WHERE " + condition;
        return DatabaseManager.executeStatement(statement, parameters) != null;
    }

    /**
     * DELETE FROM table WHERE (primary_key = keys)
     * @param keys
     * @return
     */
    public Boolean deleteRecordByKey(String[] keys) {
        return deleteRecord(getKeyEquatingCondition(), keys);
    }
}