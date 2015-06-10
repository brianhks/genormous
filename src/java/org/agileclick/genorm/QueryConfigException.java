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

package org.agileclick.genorm;

public class QueryConfigException extends Exception
	{
	private String m_option;
	private String m_query;
	private String m_message;
	
	public QueryConfigException(String option, String message)
		{
		super();
		m_option = option;
		m_message = message;
		}
		
	public void setQuery(String query) { m_query = query; }
		
	@Override
	public String getMessage()
		{
		return ("Configuration problem with option: \""+m_option+"\" in query \""+
				m_query+"\": "+m_message);
		}
	}
