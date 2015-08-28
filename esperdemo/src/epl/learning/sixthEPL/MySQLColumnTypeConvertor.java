package epl.learning.sixthEPL;
import com.espertech.esper.client.hook.*;

/**
 * Created by wxmimperio on 2015/8/28.
 */

/*
 * 1. MySQLColumnTypeConvertor必须为public类，不然无法实例化。
 * 2. Esper会为每一个EPL实例，即EPStatement提供一个Convertor实例
 */

public class MySQLColumnTypeConvertor implements SQLColumnTypeConversion {
    @Override
    public Class getColumnType(SQLColumnTypeContext sqlColumnTypeContext) {
        Class clazz = sqlColumnTypeContext.getClass();
        return clazz;
    }

    @Override
    public Object getColumnValue(SQLColumnValueContext sqlColumnValueContext) {
        Object obj = sqlColumnValueContext.getColumnValue();
        return obj;
    }

    @Override
    public Object getParameterValue(SQLInputParameterContext sqlInputParameterContext) {
        Object obj = sqlInputParameterContext.getParameterValue();
        return obj;
    }
}
