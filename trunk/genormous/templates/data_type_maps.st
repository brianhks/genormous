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

typeToGenOrmMap ::= [
	"integer":"GenOrmInt",
	"date":"GenOrmDate",
	"boolean":"GenOrmBoolean",
	"string":"GenOrmString",
	"timestamp":"GenOrmTimestamp",
	"binary":"GenOrmBinary",
	"money":"GenOrmBigDecimal",
	"version":"GenOrmDouble",
	default:"null"
	]
	
typeToJavaTypeMap ::= [
	"integer":"int",
	"date":"java.sql.Date",
	"boolean":"boolean",
	"string":"String",
	"timestamp":"java.sql.Timestamp",
	"binary":"byte[]",
	"money":"java.math.BigDecimal",
	"version":"double",
	default:"null"
	]
	
typeToJDBCTypeMap ::= [
	"integer":"Int",
	"date":"Date",
	"boolean":"Boolean",
	"string":"String",
	"timestamp":"Timestamp",
	"binary":"Bytes",
	"money":"BigDecimal",
	"version":"Double",
	default:"null"
	]
	
