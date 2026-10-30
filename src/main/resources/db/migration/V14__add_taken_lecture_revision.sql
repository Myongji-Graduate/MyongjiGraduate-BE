-- Additive migration. All writers must run revision-aware code before cache reads are enabled.
ALTER TABLE `user` ADD COLUMN taken_lecture_revision BIGINT NOT NULL DEFAULT 0;
