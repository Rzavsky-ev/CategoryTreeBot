CREATE TABLE category_tree (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       id_parent BIGINT,
       FOREIGN KEY (id_parent) REFERENCES category_tree(id) ON DELETE CASCADE

   );




