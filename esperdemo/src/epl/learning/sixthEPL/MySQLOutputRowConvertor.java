package epl.learning.sixthEPL;

import com.espertech.esper.client.hook.SQLOutputRowConversion;
import com.espertech.esper.client.hook.SQLOutputRowTypeContext;
import com.espertech.esper.client.hook.SQLOutputRowValueContext;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wxmimperio on 2015/8/28.
 */

public class MySQLOutputRowConvertor implements SQLOutputRowConversion {

    // 每行查询结果转换后的类型

    public Class getOutputRowType(SQLOutputRowTypeContext context) {
        return String.class;
    }

    // 返回转换后的内容

    public Object getOutputRow(SQLOutputRowValueContext context) {
        ResultSet result = context.getResultSet();
        Object obj1 = null;
        Object obj2 = null;
        try {
            obj1 = result.getObject("ID");
            obj2 = result.getObject("Name");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obj1 + " and " + obj2;
    }
}