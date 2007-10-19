package $package$.genorm;

import java.util.*;
import java.sql.*;

public abstract class GenOrmRecord
	{
	/**
	For debug output recompile with this value set to true
	*/
	protected static final boolean DEBUG = false;
	
	protected ArrayList<GenOrmField>   m_fields;        //Column values for this table
	protected boolean                  m_isNewRecord;   //Identifies if this is a new record
	protected int                      m_dirtyFlags;    //Identifies which field is dirty
	protected String                   m_tableName;     //Name of the table
	protected boolean                  m_isDeleted;     //Identifies if this record is deleted
	
	public GenOrmRecord(String tableName)
		{
		m_tableName = tableName;
		m_fields = new ArrayList<GenOrmField>();
		m_isNewRecord = false;
		}
		
	public Iterator<GenOrmField> getFieldIterator()
		{
		return (m_fields.iterator());
		}
		
	public boolean isNew() { return (m_isNewRecord); }
		
	public abstract void setMTS();
	public abstract void setCTS();
	
	//---------------------------------------------------------------------------
	protected boolean isEmptyReference(String s) { return (s == null); }
	protected boolean isEmptyReference(int i) { return (i == 0); }
	//---------------------------------------------------------------------------
	public void delete()
		{
		m_isDeleted = true;
		}
		
	//---------------------------------------------------------------------------
	private String createInsertStatement()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO ");
		sb.append(m_tableName);
		sb.append(" (");
		
		StringBuilder valuesSB = new StringBuilder();
		Iterator<GenOrmField> it = m_fields.iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if ((m_dirtyFlags & meta.getDirtyFlag()) != 0)
				{
				if (!first)
					{
					sb.append(",");
					valuesSB.append(",");
					}
					
				first = false;
				sb.append(meta.getFieldName());
				valuesSB.append(gof.getSQLValue());
				}
			}
			
		sb.append(") VALUES (");
		sb.append(valuesSB.toString());
		sb.append(")");
		
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private void addKeyedWhereStatement(StringBuilder sb)
		{
		sb.append(" WHERE ");
		Iterator<GenOrmField> it = m_fields.iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if (meta.isPrimaryKey())
				{
				if (!first)
					sb.append(", ");
				else
					sb.append(" ");
					
				first = false;
				sb.append(meta.getFieldName());
				sb.append(" = ");
				sb.append(gof.getSQLValue());
				}
			}
		}
		
	//---------------------------------------------------------------------------
	private String createUpdateStatement()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE ");
		sb.append(m_tableName);
		sb.append(" SET");
		Iterator<GenOrmField> it = m_fields.iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if ((m_dirtyFlags & meta.getDirtyFlag()) != 0)
				{
				if (!first)
					sb.append(", ");
				else	
					sb.append(" ");
					
				first = false;
				sb.append(meta.getFieldName());
				sb.append(" = ");
				sb.append(gof.getSQLValue());
				}
			}
			
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private String createDeleteStatement()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("DELETE FROM ");
		sb.append(m_tableName);
		
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private void runStatement(String statement)
			throws SQLException
		{
		Statement stmt = GenOrmDataSource.createStatement();
		if (DEBUG)
			System.out.println("SQL Querry: "+statement);
			
		stmt.execute(statement);
		stmt.close();
		}
		
	//---------------------------------------------------------------------------
	public void commitChanges()
			throws SQLException
		{
		//System.out.println("Committing changes");
		String query;
		
		if (m_isDeleted)
			{
			//If the record is new there is no reason to delete it as it is not there
			if (m_isNewRecord)
				return;
				
			runStatement(createDeleteStatement());
			}
		else if (m_dirtyFlags != 0)
			{
			//System.out.println("it is dirty");
			setMTS();
			
			if (m_isNewRecord)
				{
				setCTS();
				runStatement(createInsertStatement());
				}
			else
				{
				runStatement(createUpdateStatement());
				}
			}
		}
		
	//---------------------------------------------------------------------------
	public void flush()
			throws SQLException
		{
		commitChanges();
		m_dirtyFlags = 0;
		m_isNewRecord = false;
		}
	}
