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

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.dom4j.Element;

public interface QueryPlugin extends GenPlugin
	{
	//Additional code for the Query object
	public Set<String> getQueryImports(Map<String, Object> attributes);
	public String getQueryBody(Map<String, Object> attributes);
	public Set<String> getQueryImplements(Map<String, Object> attributes);
	
	//Additional code for the QueryRecord object
	public String getQueryRecordBody(Map<String, Object> attributes);
	public Set<String> getQueryRecordImplements(Map<String, Object> attributes);
	
	//Additional code for ORMObject_base
	}
