package com.adam4.dbconnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;

public class SQLRequest
{

    public String statement = null;
    private LinkedList<SQLField> fields;

    public SQLRequest(String statement)
    {
        fields = new LinkedList<>();
        this.statement = statement;
    }

    public void add(Timestamp time)
    {
        fields.add(new SQLFieldTimestamp(fields.size(), time));
    }

    public void add(String s)
    {
        fields.add(new SQLFieldString(fields.size(), s));
    }

    public void add(int x)
    {
        fields.add(new SQLFieldInt(fields.size(), x));
    }

    public void prepare(PreparedStatement pst) throws SQLException
    {
        for (SQLField field : fields)
        {
            field.addToStatement(pst);
        }
    }

    public interface SQLField
    {
        void addToStatement(PreparedStatement statement) throws SQLException;
    }

    public class SQLFieldTimestamp implements SQLField
    {
        private Timestamp time;
        private int index;

        SQLFieldTimestamp(int index, Timestamp time)
        {
            this.index = index;
            this.time = time;
        }

        @Override
        public void addToStatement(PreparedStatement statement) throws SQLException
        {
            statement.setTimestamp(index, time);
        }
    }

    public class SQLFieldString implements SQLField
    {
        private String s;
        private int index;

        SQLFieldString(int index, String s)
        {
            this.index = index;
            this.s = s;
        }

        @Override
        public void addToStatement(PreparedStatement statement) throws SQLException
        {
            statement.setString(index, s);
        }
    }

    public class SQLFieldInt implements SQLField
    {
        private int x;
        private int index;

        SQLFieldInt(int index, int x)
        {
            this.index = index;
            this.x = x;
        }

        @Override
        public void addToStatement(PreparedStatement statement) throws SQLException
        {
            statement.setInt(index, x);
        }
    }

}
