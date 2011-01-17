group gen_orm_data_maps;

javaToGenOrmMap ::= [
	"byte":"GenOrmByte",
	"int":"GenOrmInt",
	"long":"GenOrmLong",
	"boolean":"GenOrmBoolean",
	"String":"GenOrmString",
	"double":"GenOrmDouble",
	"byte[]":"GenOrmBinary",
	"java.math.BigDecimal":"GenOrmBigDecimal",
	"java.sql.Date":"GenOrmDate",
	"java.sql.Timestamp":"GenOrmTimestamp",
	default:"null"
	]
	
javaToObjectType ::= [
	"byte":"Byte",
	"int":"Integer",
	"long":"Long",
	"boolean":"Boolean",
	"String":"String",
	"double":"Double",
	"byte[]":"byte[]",
	"java.math.BigDecimal":"java.math.BigDecimal",
	"java.sql.Date":"java.sql.Date",
	"java.sql.Timestamp":"java.sql.Timestamp",
	default:"null"
	]
	
javaToJDBCMap ::= [
	"byte":"Byte",
	"int":"Int",
	"long":"Long",
	"boolean":"Boolean",
	"String":"String",
	"double":"Double",
	"byte[]":"Bytes",
	"java.math.BigDecimal":"BigDecimal",
	"java.sql.Date":"Date",
	"java.sql.Timestamp":"Timestamp",
	default:"null"
	]


	
