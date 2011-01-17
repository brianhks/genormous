package genorm.runtime;

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
