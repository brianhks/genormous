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

public class Pair<A, B>
	{
	A m_firstMember;
	B m_secondMember;
	
	public Pair()
		{
		m_firstMember = null;
		m_secondMember = null;
		}
		
	public Pair(A first, B second)
		{
		m_firstMember = first;
		m_secondMember = second;
		}
		
	public A getFirst()
		{
		return (m_firstMember);
		}
		
	public void setFirst(A first)
		{
		m_firstMember = first;
		}
		
	public B getSecond()
		{
		return (m_secondMember);
		}
		
	public void setSecond(B second)
		{
		m_secondMember = second;
		}
	}
