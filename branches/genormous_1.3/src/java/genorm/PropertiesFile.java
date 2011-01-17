package genorm;

import java.util.*;
import java.io.*;

public class PropertiesFile extends Properties
	{
	String m_FileName;
	boolean m_nonFile;
	boolean m_exists;

	public PropertiesFile(String fileName)
		{
		m_exists = false;
		m_nonFile = false;
		FileInputStream fReader;
		m_FileName = fileName;
		
		if (fileName == null)
			return;

		try
			{
			fReader = new FileInputStream(fileName);
			if (fReader != null)
				{
				this.load(fReader);
				fReader.close();
				m_exists = true;
				}
			}
		catch(Exception e)
			{
			m_nonFile = true;
			//e.printStackTrace();
			}
			
		if (m_nonFile)
			{
			try
				{
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
				if (in != null)
					{
					this.load(in);
					in.close();
					m_exists = true;
					}
				}
			catch (IOException ioe)
				{
				System.out.println("Resource failed");
				ioe.printStackTrace();
				}
			}
		}

//-------------------------------------------------------------------
	public boolean exists() { return (m_exists); }
//-------------------------------------------------------------------
	public String getString(String key)
		{
		if (key == null)
			return (null);
		return (this.getProperty(key));
		}

//-------------------------------------------------------------------
	public void setString(String key, String value)
		{
		this.setProperty(key, value);
		}

//-------------------------------------------------------------------
	public void close()
			throws IOException
		{
		FileOutputStream fWriter;

		try
			{
			fWriter = new FileOutputStream(m_FileName);
			this.store(fWriter, "properties file");
			fWriter.close();
			}
		catch(Exception e)
			{
			throw new IOException();
			}

		}
	}

