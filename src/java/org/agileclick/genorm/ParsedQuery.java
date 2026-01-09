package org.agileclick.genorm;

import org.agileclick.genorm.parser.GenOrmParser;

public interface ParsedQuery
	{
	String getName();

	String getResultType();

	GenOrmParser.Input getInput();

	GenOrmParser.Replace getReplace();

	GenOrmParser.Sql getSql();

	String getComment();

	default GenOrmParser.Return getReturn() { return null; }
	}
