# Code Generation Attributes #

This is a list of attributes that are passed to the code generation process.


# Query Gen #

| **Attribute Name** | **Type** | **Comment** |
|:-------------------|:---------|:------------|
| package | String |  |
| query | genorm.Query |  |


# Gen Orm #

| **Attribute Name** | **Type** | **Comment** |
|:-------------------|:---------|:------------|
| package | String | Package name |
| table | genorm.Table |  |
| columns | List<genorm.Column> | List of all columns |
| primaryKeys | List<genorm.Column> | List of primary key columns |
| foreignKeys | List<genorm.ForeignKeySet> | List of foreign key columns |
| uniqueColumns | List<Set<genorm.Column>> | List of sets of unique columns |
| createSQL | String | SQL statement used to create this table |
