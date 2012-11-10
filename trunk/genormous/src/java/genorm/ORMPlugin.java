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

package genorm;

import java.util.Map;
import java.util.Set;
import java.io.IOException;

public interface ORMPlugin extends GenPlugin
	{
	public Set<String> getImplements(Map<String, Object> attributes);
	
	/**
		Returns code that is added to the body of the generated ORM base class
	*/
	public String getBody(Map<String, Object> attributes) throws IOException;
	}
