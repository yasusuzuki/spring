CREATE TABLE IF NOT EXISTS TODOS (
  id int NOT NULL,
  status CHAR(4) NOT NULL,
  title VARCHAR(128) NOT NULL,
  due_date DATE NOT NULL,
  priority VARCHAR(12) NULL,
  description VARCHAR(1024) NULL,
  done_date DATE NULL
);
CREATE TABLE IF NOT EXISTS ELEMENTS (
  id int NOT NULL,
  name VARCHAR(128) NOT NULL,
);
/*
INSERT INTO TODOS VALUES (100,'PEND', 'あれをやる', '2019-3-13','★★', 'これこれ', NULL);
INSERT INTO TODOS VALUES (101,'PEND', 'これをやる', '2019-4-13','★★', 'これこれ', NULL);
INSERT INTO TODOS VALUES (102,'CLSD', 'なんとかをやる', '2019-5-13','★★', 'これこれ', '2019-5-13');
*/

