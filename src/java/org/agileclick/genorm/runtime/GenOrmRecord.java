/* 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.agileclick.genorm.runtime;

import java.util.*;
import java.sql.*;
import java.io.Serializable;

public abstract class GenOrmRecord implements Serializable
	{
	private class QueryValue
		{
		public boolean usePrev;
		public GenOrmField field;
		
		public QueryValue(GenOrmField field, boolean usePrev)
			{
			this.usePrev = usePrev;
			this.field = field;
			}
		}
		
	
	protected HashMap<String, GenOrmField>   m_fields;        //Column values for this table
	protected boolean                        m_isNewRecord;   //Identifies if this is a new record
	protected BitSet                         m_dirtyFlags;    //Identifies which field is dirty
	protected boolean                        m_isDeleted;     //Identifies if this record is deleted
	protected boolean                        m_isIgnored;     //This record should not be committed in this transaction
	protected GenOrmRecordKey                m_recordKey;
	protected transient Logger               m_logger;
	
	private ArrayList<QueryValue>            m_queryFields;   //Used for creating the prepared queries
	
	public GenOrmRecord(String tableName)
		{
		m_recordKey = new GenOrmRecordKey(tableName);
		m_fields = new HashMap<String, GenOrmField>();
		m_isNewRecord = false;
		m_queryFields = new ArrayList<QueryValue>();
		m_isIgnored = false;
		}
		
	public GenOrmRecordKey getRecordKey() { return (m_recordKey); }
	
	public Logger getLogger()
		{
		return (m_logger);
		}
		
	/**
		Returns the name of the table this record is from
	*/
	public String getTableName()
		{
		return (m_recordKey.getTableName());
		}
		
	/**
		Returns an iterator of the {@link org.agileclick.genorm.runtime.GenOrmField}s that
		are associated with this record.
	*/
	public Iterator<GenOrmField> getFieldIterator()
		{
		return (m_fields.values().iterator());
		}
		
	/**
		Returns a read only list of the fields that are associated with this record.
	*/
	public List<GenOrmField> getFields()
		{
		return (Collections.unmodifiableList(new ArrayList<GenOrmField>(m_fields.values())));
		}

	/**
		Returns the field specified field
	 */
	public GenOrmField getField(String columnName)
		{
		return (m_fields.get(columnName));
		}
		
	//---------------------------------------------------------------------------
	/**
		Returns all fields that have been modified
	*/
	public List<GenOrmField> getDirtyFields()
		{
		List<GenOrmField> ret = new ArrayList<GenOrmField>();
		Iterator<GenOrmField> it = m_fields.values().iterator();
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if (m_dirtyFlags.get(meta.getDirtyFlag()))
				{
				ret.add(gof);
				}
			}
			
		return (ret);
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
	
	
	/**
		This returns the GenOrmConnection object that is specific to the generated
		code.
	*/
	public abstract GenOrmConnection getGenOrmConnection();
	
	/**
		Returns the plugin used to create the SQL for this set of generated objects.
	*/
	public abstract String getFieldEscapeString();
	
	//---------------------------------------------------------------------------
	protected boolean isEmptyReference(String s) { return (s == null); }
	protected boolean isEmptyReference(int i) { return (i == 0); }
	//---------------------------------------------------------------------------
	/**
		Deletes this record
	*/
	public void delete()
		{
		if (!m_isDeleted)
			{
			m_isDeleted = true;
			
			//If no transaction exists then we flush the record
			if (!getGenOrmConnection().addToTransaction(this))
				flush();
			}
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
		return (!m_dirtyFlags.isEmpty());
		}


	/**
	 Marks the specified field as dirty for this record so it will be updated with
	 the next flush.
	 @param field
	 */
	//---------------------------------------------------------------------------
	public void setFieldDirty(GenOrmField field)
		{
		m_dirtyFlags.set(field.getFieldMeta().getDirtyFlag());
		}
		
	//---------------------------------------------------------------------------
	/**
		Forces this record to be dirty which will cause all values to be commited 
		to the database upon commit of the transaction
	*/
	public void setDirty()
		{
		if (m_dirtyFlags.isEmpty())
			getGenOrmConnection().addToTransaction(this);
			
		//This will mark all attributes as dirty
		m_dirtyFlags.set(0, m_fields.size());
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
	/**
		Returns a list of foreign keys for this record
	*/
	public abstract List<GenOrmRecordKey> getForeignKeys();
	
	//---------------------------------------------------------------------------
	protected void addField(String name, GenOrmField field)
		{
		m_fields.put(name, field);
		if (field.getFieldMeta().isPrimaryKey())
			m_recordKey.addKeyField(field.getFieldMeta().getFieldName(), field);
		}
	
	//---------------------------------------------------------------------------
	private String createInsertStatement(GenOrmConnection con)
		{
		StringBuilder sb = new StringBuilder();
		m_queryFields.clear();
		
		sb.append("INSERT INTO ");
		sb.append(m_recordKey.getTableName());
		sb.append(" (");
		
		StringBuilder valuesSB = new StringBuilder();
		Iterator<GenOrmField> it = m_fields.values().iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			//System.out.println("looking at "+meta.getFieldName());
			//System.out.println(gof);
			
			boolean addForeignKey = false;
			if (meta.isForeignKey() && (con != null))
				{
				//This is gauranteed if it is a foreign key
				GenOrmRecordKey key = gof.getRecordKey();
				
				//Looking to see if the record is part of the transaction
				GenOrmRecord rec = con.getCachedRecord(key);
				if ((rec == null) || (!rec.isNew()) || (!rec.isDirty()))
					{
					//System.out.println("Key = "+key);
					//System.out.println("Add foreign key "+meta.getFieldName());
					addForeignKey = true;
					}
				}
			
			if (m_dirtyFlags.get(meta.getDirtyFlag()) &&
					( (meta.isPrimaryKey()) || (!meta.isForeignKey()) || addForeignKey ) )
				{
				m_dirtyFlags.clear(meta.getDirtyFlag()); //Clear dirty flag on value
				if (!first)
					{
					sb.append(",");
					valuesSB.append(",");
					}
					
				first = false;
				sb.append(getFieldEscapeString());
				sb.append(meta.getFieldName());
				sb.append(getFieldEscapeString());
				m_queryFields.add(new QueryValue(gof, false));
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
		Iterator<GenOrmField> it = m_fields.values().iterator();
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
					sb.append(" WHERE ");
					
				first = false;
				sb.append(getFieldEscapeString());
				sb.append(meta.getFieldName());
				sb.append(getFieldEscapeString());
				sb.append(" = ");
				sb.append("?");
				m_queryFields.add(new QueryValue(gof, true));
				}
			}
		}
		
	//---------------------------------------------------------------------------
	private String createUpdateStatement()
		{
		StringBuilder sb = new StringBuilder();
		m_queryFields.clear();
		
		sb.append("UPDATE ");
		sb.append(m_recordKey.getTableName());
		sb.append(" SET");
		Iterator<GenOrmField> it = m_fields.values().iterator();
		boolean first = true;
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			GenOrmFieldMeta meta = gof.getFieldMeta();
			if (m_dirtyFlags.get(meta.getDirtyFlag()))
				{
				if (!first)
					sb.append(", ");
				else	
					sb.append(" ");
					
				first = false;
				sb.append(getFieldEscapeString());
				sb.append(meta.getFieldName());
				sb.append(getFieldEscapeString());
				sb.append(" = ");
				sb.append("?");
				m_queryFields.add(new QueryValue(gof, false));
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
		sb.append(m_recordKey.getTableName());
		
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private boolean runStatement(String statement)
			throws SQLException
		{
		boolean ret = false;
		if (m_logger != null && m_logger.isDebug())
			{
			m_logger.debug("SQL Query: "+statement);
			}
		//System.out.println(statement);
			
		PreparedStatement stmt = getGenOrmConnection().prepareStatement(statement);
		
		try
			{
			for (int I = 0; I < m_queryFields.size(); I++)
				{
				if (m_logger != null && m_logger.isDebug())
					{
					GenOrmField field = m_queryFields.get(I).field;
					m_logger.debug(field.getFieldMeta().getFieldName()+" : "+field.toString());
					}
					
				QueryValue qv = m_queryFields.get(I);
				
				if (qv.usePrev)
					{
					//System.out.println("USING PREV "+qv.field.getPrevValue());
					qv.field.placePrevValue(stmt, I+1);
					}
				else
					{
					//System.out.println("USING CURRENT");
					qv.field.placeValue(stmt, I+1);
					}
				}
				
			int result = stmt.executeUpdate();
			//System.out.println(result);
				
			ret = (result != 0);
			}
		finally
			{
			stmt.close();
			}
			
		return (ret);
		}
		
	//---------------------------------------------------------------------------
	/*package*/ void createIfNew(GenOrmConnection con)
			throws SQLException
		{
		if ((m_isNewRecord) && (!m_isDeleted) && (!m_isIgnored))
			{
			setCTS();
			setMTS();
			runStatement(createInsertStatement(con));
			m_isNewRecord = false;
			}
		}
		
	//---------------------------------------------------------------------------
	/*package*/ boolean commitChanges()
			throws SQLException
		{
		boolean ret = true;
		String query;
		
		if (m_isIgnored)
			return(true);
		if (m_isDeleted)
			{
			//If the record is new there is no reason to delete it as it is not there
			if (m_isNewRecord)
				return(true);
				
			ret = runStatement(createDeleteStatement());
			}
		else if (!m_dirtyFlags.isEmpty())
			{
			//System.out.println("it is dirty");
			setMTS();
			
			runStatement(createUpdateStatement());
			}
			
		return (ret);
		}
		
	//---------------------------------------------------------------------------
	/**
		If there is a connection on the thread this will do nothing.  The idea is 
		that if there is a connection this record will be flushed when the transaction
		is committed.
		
		If no connection exits then this record will be flushed
		
		@return Returns the same as flush
	*/
	public boolean flushIfNonTransaction()
		{
		boolean ret = true;
		
		if (getGenOrmConnection() instanceof GenOrmDudConnection)
			ret = flush();
			
		return (ret);
		}
		
	//---------------------------------------------------------------------------
	/**
		Flush any changes made to this record.  The record will be inserted if 
		it is new.
		
		@return In all cases except a delete this will return true.  In the case
		where the record is deleted it will return true if the record was in the db
		and false if it wasn't.
	*/
	public boolean flush()
		{
		boolean ret = false;
	
		try
			{
			GenOrmConnection con = getGenOrmConnection();
			//System.out.println("Flushing record for "+m_tableName+" "+toString());
			createIfNew(con);
			ret = commitChanges();
			m_dirtyFlags.clear();
			m_isNewRecord = false;
			}
		catch (SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		
		return (ret);
		}
		
	//---------------------------------------------------------------------------
	/**
		Returns the combined hash code of all primary keys of this record
	*/
	@Override
	public int hashCode()
		{
		return (m_recordKey.hashCode());
		}
		
	//---------------------------------------------------------------------------
	/**
		Equality is based on the table name and values of the primary keys only
	*/
	@Override
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmRecord))
			return (false);
			
		return (m_recordKey.equals(((GenOrmRecord)obj).getRecordKey()));
		}
	}
