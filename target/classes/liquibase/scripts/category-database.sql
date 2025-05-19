CREATE TABLE category_tree (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       parent_id BIGINT,
       FOREIGN KEY (parent_id) REFERENCES category_tree(id) ON DELETE CASCADE
   );




