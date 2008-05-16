group gen_orm_data_maps;

javaToGenOrmMap ::= [
	"int":"GenOrmInt",
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
	"int":"Integer",
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
	"int":"Int",
	"boolean":"Boolean",
	"String":"String",
	"double":"Double",
	"byte[]":"Bytes",
	"java.math.BigDecimal":"BigDecimal",
	"java.sql.Date":"Date",
	"java.sql.Timestamp":"Timestamp",
	default:"null"
	]


	
