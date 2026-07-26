ALTER TABLE parsing_text_history
	ADD COLUMN tracking_code VARCHAR(36) NULL COMMENT '비회원 검사 문의용 추적 코드',
	ADD COLUMN requester_type VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '회원/비회원 요청 구분';

CREATE UNIQUE INDEX uk_parsing_text_history_tracking_code
	ON parsing_text_history (tracking_code);

CREATE INDEX idx_parsing_text_history_requester_result_created
	ON parsing_text_history (requester_type, parsing_result, created_at);
