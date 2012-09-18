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

package genorm.runtime;

import java.sql.*;
import java.util.*;

public interface GenOrmQueryResultSet
	{
	public void close();
	/**
		Returns the result set as an array and cloases the result set
		@param maxRows if the result set contains more than the max
			rows an exception is thrown.
	*/
	public List<? extends GenOrmQueryRecord> getArrayList(int maxRows);
	/**
		Returns the result set as an array and cloases the result set
	*/
	public List<? extends GenOrmQueryRecord> getArrayList();
	public java.sql.ResultSet getResultSet();
	public GenOrmQueryRecord getRecord();
	public GenOrmQueryRecord getOnlyRecord();
	public boolean next();
	}
