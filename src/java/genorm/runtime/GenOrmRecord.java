package genorm.runtime;

import java.util.*;
import java.sql.*;

public abstract class GenOrmRecord
	{
	protected ArrayList<GenOrmField>   m_fields;        //Column values for this table
	protected boolean                  m_isNewRecord;   //Identifies if this is a new record
	protected int                      m_dirtyFlags;    //Identifies which field is dirty
	protected boolean                  m_isDeleted;     //Identifies if this record is deleted
	protected boolean                  m_isIgnored;     //This record should not be committed in this transaction
	protected GenOrmRecordKey          m_recordKey;
	protected Logger                   m_logger;
	
	private ArrayList<GenOrmField>     m_queryFields;   //Used for creating the prepared queries
	
	public GenOrmRecord(String tableName)
		{
		m_recordKey = new GenOrmRecordKey(tableName);
		m_fields = new ArrayList<GenOrmField>();
		m_isNewRecord = false;
		m_queryFields = new ArrayList<GenOrmField>();
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
	
	
	/**
		
	*/
	public abstract GenOrmConnection getGenOrmConnection();
	
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
			GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
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
		return (m_dirtyFlags != 0);
		}
		
	//---------------------------------------------------------------------------
	/**
		Forces this record to be dirty which will cause all values to be commited 
		to the database upon commit of the transaction
	*/
	public void setDirty()
		{
		if (m_dirtyFlags == 0)
			GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
			
		//This will mark all attributes as dirty
		m_dirtyFlags = -1;
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
	protected void addField(GenOrmField field)
		{
		m_fields.add(field);
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
		Iterator<GenOrmField> it = m_fields.iterator();
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
			
			if (((m_dirtyFlags & meta.getDirtyFlag()) != 0) &&
					( (meta.isPrimaryKey()) || (!meta.isForeignKey()) || addForeignKey ) )
				{
				m_dirtyFlags ^= meta.getDirtyFlag(); //Clear dirty flag on value
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
		sb.append(m_recordKey.getTableName());
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
		sb.append(m_recordKey.getTableName());
		
		addKeyedWhereStatement(sb);
			
		return (sb.toString());
		}
		
	//---------------------------------------------------------------------------
	private boolean runStatement(String statement)
			throws SQLException
		{
		boolean ret = false;
		if (m_logger.isDebug())
			{
			m_logger.debug("SQL Query: "+statement);
			}
			
		PreparedStatement stmt = GenOrmDataSource.prepareStatement(statement);
		
		for (int I = 0; I < m_queryFields.size(); I++)
			{
			if (m_logger.isDebug())
				{
				GenOrmField field = m_queryFields.get(I);
				m_logger.debug(field.getFieldMeta().getFieldName()+" : "+field.toString());
				}
				
			m_queryFields.get(I).placeValue(stmt, I+1);
			}
			
		ret = (stmt.executeUpdate() != 0);
			
		stmt.close();
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
		else if (m_dirtyFlags != 0)
			{
			//System.out.println("it is dirty");
			setMTS();
			
			runStatement(createUpdateStatement());
			}
			
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
			throws SQLException
		{
		boolean ret = false;
		GenOrmConnection con = GenOrmDataSource.getGenOrmConnection();
		//System.out.println("Flushing record for "+m_tableName+" "+toString());
		createIfNew(con);
		ret = commitChanges();
		m_dirtyFlags = 0;
		m_isNewRecord = false;
		
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