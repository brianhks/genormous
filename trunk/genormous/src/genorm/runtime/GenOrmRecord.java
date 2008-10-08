package genorm.runtime;

import java.util.*;
import java.sql.*;

public abstract class GenOrmRecord
	{
	protected static boolean DEBUG = false;
	
	protected ArrayList<GenOrmField>   m_fields;        //Column values for this table
	protected boolean                  m_isNewRecord;   //Identifies if this is a new record
	protected int                      m_dirtyFlags;    //Identifies which field is dirty
	protected String                   m_tableName;     //Name of the table
	protected boolean                  m_isDeleted;     //Identifies if this record is deleted
	protected boolean                  m_isIgnored;     //This record should not be committed in this transaction
	
	private ArrayList<GenOrmField>     m_queryFields;   //Used for creating the prepared queries
	
	public GenOrmRecord(String tableName)
		{
		m_tableName = tableName;
		m_fields = new ArrayList<GenOrmField>();
		m_isNewRecord = false;
		m_queryFields = new ArrayList<GenOrmField>();
		}
		
	/**
		Returns the name of the table this record is from
	*/
	public String getTableName()
		{
		return (m_tableName);
		}
		
	/**
		Returns an iterator of the {@link genorm.runtime.GenOrmField}s that
		are associated with this record.
	*/
	public Iterator<GenOrmField> getFieldIterator()
		{
		return (m_fields.iterator());
		}
		
	/**
		Identifies this record as a new record that needs to be added to the database
	*/
	public boolean isNew() { return (m_isNewRecord); }
		
	/**
		This method is called whenever a modification is made to this record.
		The purpose is for setting a modification timestamp.
	*/
	public abstract void setMTS();
	
	/**
		This method is called when a new record is created.  The purpose is
		to set a creation timestamp for this record.
	*/
	public abstract void setCTS();
	
	//---------------------------------------------------------------------------
	protected boolean isEmptyReference(String s) { return (s == null); }
	protected boolean isEmptyReference(int i) { return (i == 0); }
	//---------------------------------------------------------------------------
	/**
		Deletes this record
	*/
	public void delete()
		{
		m_isDeleted = true;
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		}
		
	//---------------------------------------------------------------------------
	/**
		Returns if the record has been deleted by a previous call to {@link #delete()}
	*/
	public boolean isDeleted() { return (m_isDeleted); }
	
	//---------------------------------------------------------------------------
	/**
		Returns if this record has been modified
	*/
	public boolean isDirty()
		{
		return (m_dirtyFlags != 0);
		}
		
	//---------------------------------------------------------------------------
	/**
		Forces this record to be dirty which will cause all values to be commited 
		to the database upon commit of the transaction
	*/
	public void setDirty()
		{
		//This will mark all attributes as dirty
		m_dirtyFlags = -1;
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		}
	//---------------------------------------------------------------------------
	/**
		Sets this record to be ignored.  Ignored records are not commited to the 
		database.
	*/
	public void setIgnored(boolean ignore) { m_isIgnored = ignore; }
	
	//---------------------------------------------------------------------------
	/**
		Identifies if this record is ignored or not.
	*/
	public boolean isIgnored() { return (m_isIgnored); }
	
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
				sb.append("\"");
				sb.append(meta.getFieldName());
				sb.append("\"");
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
				sb.append("\"");
				sb.append(meta.getFieldName());
				sb.append("\"");
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
				sb.append("\"");
				sb.append(meta.getFieldName());
				sb.append("\"");
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
	/*package*/ void createIfNew()
			throws SQLException
		{
		if ((m_isNewRecord) && (!m_isDeleted) && (!m_isIgnored))
			{
			setCTS();
			setMTS();
			runStatement(createInsertStatement());
			}
		}
		
	//---------------------------------------------------------------------------
	/*package*/ void commitChanges()
			throws SQLException
		{
		String query;
		
		if (m_isIgnored)
			return;
		
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
	/**
		Flush any changes made to this record.  The record will be inserted if 
		it is new.
	*/
	public void flush()
			throws SQLException
		{
		//System.out.println("Flushing record for "+m_tableName+" "+toString());
		createIfNew();
		commitChanges();
		m_dirtyFlags = 0;
		m_isNewRecord = false;
		}
		
	//---------------------------------------------------------------------------
	/**
		Returns the combined hash code of all primary keys of this record
	*/
	@Override
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
	@Override
	public abstract boolean equals(Object obj);
	}
