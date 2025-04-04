package com.team8.project2.global.exception;

import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger; // ✅ 추가: 로깅을 위해 Logger import
import org.slf4j.LoggerFactory; // ✅ 추가: LoggerFactory import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team8.project2.global.dto.Empty;
import com.team8.project2.global.dto.RsData;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ✅ 추가: 예외 로깅을 위한 Logger 인스턴스
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		// ✅ 수정: 에러 메시지 포맷 개선 (필드명 : 에러코드 : 기본 메시지)
		String message = e.getBindingResult().getFieldErrors()
				.stream()
				.map(fe -> fe.getField() + " : " + fe.getCode() + " : " + fe.getDefaultMessage())
				.sorted()
				.collect(Collectors.joining("\n"));

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(
						new RsData<>(
								"400-1",
								message
						)
				);
	}

	@ResponseStatus // SpringDoc에서 메서드 인식
	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<RsData<Void>> handleServiceException(ServiceException ex) { // ✅ 수정: 메서드 이름 변경(ServiceExceptionHandle -> handleServiceException)
		return ResponseEntity
				.status(ex.getStatusCode())
				.body(
						new RsData<>(
								ex.getCode(),
								ex.getMsg()
						)
				);
	}

	/** 400 - Bad Request (BadRequestException 및 IllegalArgumentException) */
	@ResponseStatus
	@ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
	public ResponseEntity<RsData<Void>> handleBadRequestException(RuntimeException e) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new RsData<>("400-2", e.getMessage()));
	}

	/** 404 - Not Found (NotFoundException) */
	@ResponseStatus
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<RsData<Void>> handleNotFoundException(NotFoundException e) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new RsData<>("404-1", e.getMessage()));
	}

	/** 401 - Unauthorized */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<RsData<Void>> handleAccessDeniedException(AccessDeniedException ex) {
		// 예외 발생 시 반환할 메시지와 상태 코드 정의
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new RsData<>("401-1","접근이 거부되었습니다. 로그인 상태를 확인해 주세요."));
	}

	/** 500 - Internal Server Error */
	@ResponseStatus
	@ExceptionHandler(Exception.class)
	public ResponseEntity<RsData<Void>> handleGlobalException(Exception e) {
		// ✅ 추가: 발생한 예외를 로깅합니다.
		logger.error("Unhandled exception occurred", e);

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new RsData<>("500-1", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."));
	}
}
