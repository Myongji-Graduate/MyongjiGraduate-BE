-- Migration: Add failure_reason and failure_details columns to parsing_text_history table
-- Version: V1
-- Description: 스키마 확장 - 실패한 성적표 분석을 위한 failure_reason, failure_details 컬럼 추가

-- Add failure_reason column
ALTER TABLE parsing_text_history 
ADD COLUMN failure_reason VARCHAR(100) NULL COMMENT '실패 원인 (FailureReason enum 값)' 
AFTER parsing_result;

-- Add failure_details column
ALTER TABLE parsing_text_history 
ADD COLUMN failure_details TEXT NULL COMMENT '실패 상세 정보' 
AFTER failure_reason;

