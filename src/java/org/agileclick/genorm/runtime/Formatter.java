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
import java.sql.Timestamp;
import java.math.BigDecimal;

public interface Formatter
	{
	public String toString(String name, String str);
	public String toString(String name, int val);
	public String toString(String name, java.util.Date date);
	public String toString(String name, boolean bool);
	public String toString(String name, double val);
	public String toString(String name, BigDecimal bd);
	public String toString(String name, Timestamp ts);
	public String toString(String name, byte[] bytes);
	public String toString(String name, java.util.UUID val);
	public void setLocale(Locale locale);
	}
