package com.team8.project2.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsData<T> {
	private String code;
	private String msg;
	private T data;

	public RsData(String code, String msg) {
		this(code, msg, null);
	}

	@JsonIgnore
	public int getStatusCode() {
		String statusCodeStr = code.split("-")[0];
		return Integer.parseInt(statusCodeStr);
	}

	// 성공 응답 생성 메서드 추가
	public static <T> RsData<T> success(T data) {
		return new RsData<>("200-1", "Success", data);
	}
	// custom success response
	public static <T> RsData<T> success(String msg, T data) {
		return new RsData<>("200-1", msg, data);
	}


	// 실패 응답 생성 메서드 추가
	public static <T> RsData<T> fail(String msg) {
		return new RsData<>("400-1", msg, null);
	}
	// custom fail response
	public static <T> RsData<T> fail(String code, String msg) {
		return new RsData<>(code, msg, null);
	}
}
