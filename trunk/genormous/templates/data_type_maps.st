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
	"java.util.UUID":"GenOrmUUID",
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
	"java.util.UUID":"java.util.UUID",
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
	"java.util.UUID":"Object",
	default:"null"
	]
	

	
