package com.example.mamolog.dto;

// FullCalendar用のDTO
public class CalendarTodoDto {
	private String title;
	private String start;	// SIO形式文字列: "2025-09-27"
	
	public CalendarTodoDto(String title, String start){
		this.title = title;
		this.start = start;
	}
	
	public String getTitle() { return title; }
	public String getStart() { return start; }
}