CREATE CACHED TABLE author (
	"author_id" INT  NOT NULL,
	"name" VARCHAR  NULL,
	PRIMARY KEY ("author_id")
	);

CREATE CACHED TABLE book (
	"author" INT  NULL,
	"title" VARCHAR  NULL,
	"isbn" VARCHAR  NULL,
	FOREIGN KEY ("author")
		REFERENCES author ("author_id")
	);

