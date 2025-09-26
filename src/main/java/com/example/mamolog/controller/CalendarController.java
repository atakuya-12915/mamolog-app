package com.example.mamolog.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final TodoService todoService;

    public CalendarController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ────────── 月間カレンダー画面表示 ──────────
    @GetMapping
    public String showCalendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model
    ) {
        // 今日の日付
        LocalDate today = LocalDate.now();
        int displayYear = (year != null) ? year : today.getYear();
        int displayMonth = (month != null) ? month : today.getMonthValue();

        // 表示年月
        YearMonth yearMonth = YearMonth.of(displayYear, displayMonth);

        // 月初・月末
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        // ────────── 月内の日付リスト作成（空白セル含む） ──────────
        List<LocalDate> days = new ArrayList<>();     

        // 月初の曜日に応じた空白セル追加（日曜始まり）
        DayOfWeek firstDow = firstDay.getDayOfWeek();
        int emptyStart = firstDow.getValue() % 7; 	// 日曜=0, 月曜=1...
        for (int i = 0; i < emptyStart; i++) {
            days.add(null);						 	// 必要な数の空白セルを追加
        }

        // 月の日付を追加
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            days.add(date);
        }

        // 最後の週を7列に揃える空白セル
        int remaining = 7 - (days.size() % 7);		// 必要な空白セルの数を計算
        if (remaining < 7) {
            for (int i = 0; i < remaining; i++) {
                days.add(null);						// null の分だけ空白セルを追加
            }
        }

        // ────────── 週ごとのリストに変換 ──────────
        List<List<LocalDate>> weeks = new ArrayList<>();
        
        for (int i = 0; i < days.size(); i += 7) {
            weeks.add(new ArrayList<>(days.subList(i, i + 7)));
        }

        // ────────── Todoマップ作成（日付ごとのTodoリスト） ──────────
        Map<LocalDate, List<Todo>> todoMap = new HashMap<>();
        for (LocalDate date : days) {
            if (date != null) {
                List<Todo> todos = todoService.getTodosByDate(date, false); // 未完了
                todos.addAll(todoService.getTodosByDate(date, true));       // 完了
                todoMap.put(date, todos);
            }
        }

        // Viewへ渡す情報
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("weeks", weeks);
        model.addAttribute("todoMap", todoMap);

        return "calendar/calendar"; // calendar/calendar.html
    }

    // ────────── 日付クリック時の詳細表示 ──────────
    @GetMapping("/detail")
    public String showDetail(@RequestParam("date") String date, Model model) {
        LocalDate selectedDate = LocalDate.parse(date);
        List<Todo> todos = todoService.findByDate(selectedDate); // その日の全タスク
        if (todos == null) todos = new ArrayList<>();			 // Todoがない場合は空配列をViewに渡す

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("todos", todos);

        return "calendar/detail"; // calendar/detail.html
    }
}
