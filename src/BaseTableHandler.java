import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class BaseTableHandler {
    public String tableName;
    public String[] columns;
    public String[] primaryKeys;

    public BaseTableHandler(String tableName, String[] columns, String[] primaryKeys) {
        this.tableName = tableName;
        this.columns = columns;
        this.primaryKeys = primaryKeys;
    }

    private String getKeyEquatingCondition() {
        String condition = "";
        for (int i = 0; i < primaryKeys.length; i++) {
            condition += primaryKeys[i] + " = ?";
            if (i < primaryKeys.length - 1) {
                condition += " AND ";
            }
        }
        return condition;
    }

    public ResultSet getRecords(String condition, ArrayList<String> parameters, String orderBy) {
        String statement = "SELECT * FROM " + tableName;
        if (condition != null) {
            statement += " WHERE " + condition;
        }
        if (orderBy != null) {
            statement += " ORDER BY " + orderBy;
        }
        return DatabaseManager.executeStatement(statement, parameters);
    }

    public ResultSet getRecords(String condition, ArrayList<String> parameters) {
        return getRecords(condition, parameters, null);
    }

    public ResultSet getRecordByKey(String[] keys) {
        return getRecords(getKeyEquatingCondition(), new ArrayList<String>(Arrays.asList(keys)));
    }

    public Boolean insertRecord(String[] specificColumns, ArrayList<String> values) {
        String statement = "INSERT INTO " + tableName + " (";
        for (int i = 0; i < specificColumns.length; i++) {
            statement += specificColumns[i];
            if (i < specificColumns.length - 1) {
                statement += ", ";
            }
        }
        statement += ") VALUES (";
        for (int i = 0; i < specificColumns.length; i++) {
            statement += "?";
            if (i < specificColumns.length - 1) {
                statement += ", ";
            }
        }
        statement += ")";
        return DatabaseManager.executeStatement(statement, values) != null;
    }

    public Boolean insertRecord(ArrayList<String> values) {
        return insertRecord(columns, values);
    }

    public Boolean updateRecord(String[] specificColumns, ArrayList<String> newValues, String condition,
            ArrayList<String> conditionParameters) {
        String statement = "UPDATE " + tableName + " SET ";
        for (int i = 0; i < specificColumns.length; i++) {
            statement += specificColumns[i] + " = ?";
            if (i < specificColumns.length - 1) {
                statement += ", ";
            }
        }
        statement += " WHERE " + condition;
        ArrayList<String> allParameters = new ArrayList<>();
        allParameters.addAll(newValues);
        allParameters.addAll(conditionParameters);
        return DatabaseManager.executeStatement(statement, allParameters) != null;
    }

    public Boolean updateRecordByKey(String[] specificColumns, ArrayList<String> newValues, String[] keys) {
        return updateRecord(specificColumns, newValues, getKeyEquatingCondition(),
                new ArrayList<String>(Arrays.asList(keys)));
    }

    public Boolean deleteRecord(String condition, ArrayList<String> parameters) {
        String statement = "DELETE FROM " + tableName + " WHERE " + condition;
        return DatabaseManager.executeStatement(statement, parameters) != null;
    }

    public Boolean deleteRecordByKey(String[] keys) {
        return deleteRecord(getKeyEquatingCondition(), new ArrayList<String>(Arrays.asList(keys)));
    }
}