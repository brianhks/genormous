group gen_orm_data_maps;

license() ::=<<
$! 
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
!$
>>

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
	

	
