package com.adam4.dbconnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public void add(java.sql.Date date)
    {
        fields.add(new SQLFieldDate(fields.size(), date));
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

    public class SQLFieldDate implements SQLField
    {
        private Date date;
        private int index;

        SQLFieldDate(int index, java.sql.Date date)
        {
            this.index = index;
            this.date = date;
        }

        @Override
        public void addToStatement(PreparedStatement statement) throws SQLException
        {
            statement.setDate(index, date);
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

    public void add(java.util.Date date)
    {
        // convert from java date to SQL date
        add(new java.sql.Date(date.getTime()));
    }

}
