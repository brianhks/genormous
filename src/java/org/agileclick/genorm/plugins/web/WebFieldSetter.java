package org.agileclick.genorm.plugins.web;

public interface WebFieldSetter<T>
{
	WebFieldSetter<T> setWebFieldConverter(WebFieldConverter converter);
	T setFields(T obj, java.util.Map<String, String[]> data);
}
