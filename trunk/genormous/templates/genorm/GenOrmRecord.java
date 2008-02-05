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
	
	private ArrayList<GenOrmField>     m_queryFields;   //Used for creating the prepared queries
	
	public GenOrmRecord(String tableName)
		{
		m_tableName = tableName;
		m_fields = new ArrayList<GenOrmField>();
		m_isNewRecord = false;
		m_queryFields = new ArrayList<GenOrmField>();
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
		m_queryFields.clear();
		
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
			if (meta.isPrimaryKey() || (!meta.isForeignKey() && 
					((m_dirtyFlags & meta.getDirtyFlag()) != 0)))
			//if (!meta.isForeignKey())
			//if ((m_dirtyFlags & meta.getDirtyFlag()) != 0)
				{
				m_dirtyFlags ^= meta.getDirtyFlag(); //Clear dirty flag on key
				if (!first)
					{
					sb.append(",");
					valuesSB.append(",");
					}
					
				first = false;
				sb.append(meta.getFieldName());
				m_queryFields.add(gof);
				valuesSB.append("?");
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
					sb.append(" AND ");
				else
					sb.append(" ");
					
				first = false;
				sb.append(meta.getFieldName());
				sb.append(" = ");
				sb.append("?");
				m_queryFields.add(gof);
				}
			}
		}
		
	//---------------------------------------------------------------------------
	private String createUpdateStatement()
		{
		StringBuilder sb = new StringBuilder();
		m_queryFields.clear();
		
		sb.append("UPDATE ");
		sb.append(m_tableName);
		sb.append(" SET");
		Iterator<GenOrmField> it = m_fields.iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if ((!meta.isPrimaryKey()) && (m_dirtyFlags & meta.getDirtyFlag()) != 0)
				{
				if (!first)
					sb.append(", ");
				else	
					sb.append(" ");
					
				first = false;
				sb.append(meta.getFieldName());
				sb.append(" = ");
				sb.append("?");
				m_queryFields.add(gof);
				}
			}
			
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private String createDeleteStatement()
		{
		StringBuilder sb = new StringBuilder();
		m_queryFields.clear();
		
		sb.append("DELETE FROM ");
		sb.append(m_tableName);
		
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private void runStatement(String statement)
			throws SQLException
		{
		if (DEBUG)
			System.out.println("SQL Querry: "+statement);
			
		PreparedStatement stmt = GenOrmDataSource.prepareStatement(statement);
		
		for (int I = 0; I < m_queryFields.size(); I++)
			{
			if (DEBUG)
				{
				GenOrmField field = m_queryFields.get(I);
				System.out.println(field.getFieldMeta().getFieldName()+" : "+field.toString());
				}
				
			m_queryFields.get(I).placeValue(stmt, I+1);
			}
			
		stmt.execute();
		stmt.close();
		}
		
	//---------------------------------------------------------------------------
	public void createIfNew()
			throws SQLException
		{
		if ((m_isNewRecord) && (!m_isDeleted))
			{
			setCTS();
			setMTS();
			runStatement(createInsertStatement());
			}
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
			
			runStatement(createUpdateStatement());
			}
		}
		
	//---------------------------------------------------------------------------
	public void flush()
			throws SQLException
		{
		createIfNew();
		commitChanges();
		m_dirtyFlags = 0;
		m_isNewRecord = false;
		}
		
	//---------------------------------------------------------------------------
	/**
		Returns the combined hash code of all primary keys of this record
	*/
	public int hashCode()
		{
		Iterator<GenOrmField> it = m_fields.iterator();
		int hashCode = 1;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if (meta.isPrimaryKey())
				{
				//Same hash code calculation as used in a java.util.List
				hashCode = 31 * hashCode + gof.hashCode();
				}
			}
			
		return (hashCode);
		}
		
	//---------------------------------------------------------------------------
	/**
		Equality is based on the values of the primary keys only
	*/
	public abstract boolean equals(Object obj);
	}
