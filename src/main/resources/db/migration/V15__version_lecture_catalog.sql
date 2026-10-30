-- MySQL only. Metadata and its revision commit/roll back together, including direct SQL imports.
CREATE TABLE lecture_catalog_revision (
    id INT NOT NULL PRIMARY KEY,
    revision BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB;
INSERT INTO lecture_catalog_revision (id, revision) VALUES (1, 0);

CREATE TRIGGER lecture_catalog_revision_insert AFTER INSERT ON lecture
FOR EACH ROW UPDATE lecture_catalog_revision SET revision = revision + 1 WHERE id = 1;

CREATE TRIGGER lecture_catalog_revision_update AFTER UPDATE ON lecture
FOR EACH ROW UPDATE lecture_catalog_revision SET revision = revision + 1 WHERE id = 1;

CREATE TRIGGER lecture_catalog_revision_delete AFTER DELETE ON lecture
FOR EACH ROW UPDATE lecture_catalog_revision SET revision = revision + 1 WHERE id = 1;
