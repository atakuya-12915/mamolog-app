package com.example.mamolog.controller.todo;

import java.time.LocalDate;

import org.springframework.data.domain.Page;                    // ページング情報を扱うクラス
import org.springframework.data.domain.PageRequest;          	// ページ番号・ページサイズを指定するクラス
import org.springframework.data.domain.Pageable;              	// ページング用インターフェース
import org.springframework.data.domain.Sort;                  	// ソート情報を指定するクラス
import org.springframework.format.annotation.DateTimeFormat;   	// 日付のパラメータ変換用
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService; // Service層をDI（依存性注入）で取得

    public TodoController(TodoService todoService) {
        this.todoService = todoService;  // コンストラクタでServiceをセット
    }

    // ────────── 一覧表示（未完了/完了 + ページネーション） ──────────
    @GetMapping
    public String listTodos(
            Model model,
            @RequestParam(defaultValue = "0") int incompletePage, // 未完了タスクの現在ページ（0始まり）
            @RequestParam(defaultValue = "0") int completePage    // 完了タスクの現在ページ（0始まり）
    ) {
        // 1ページあたり10件表示、未完了は期限日昇順
        Pageable incompletePageable = PageRequest.of(incompletePage, 10, Sort.by("dueDate").ascending());
        // 完了は作成日時降順で並べる
        Pageable completePageable = PageRequest.of(completePage, 10, Sort.by("createdAt").descending());

        // Service層でページ単位の未完了タスクを取得
        Page<Todo> incompleteTodos = todoService.getTodosPage(false, incompletePageable);
        // Service層でページ単位の完了タスクを取得
        Page<Todo> completeTodos = todoService.getTodosPage(true, completePageable);

        // Viewに渡す
        model.addAttribute("incompleteTodos", incompleteTodos); // 未完了タスク
        model.addAttribute("completeTodos", completeTodos);     // 完了タスク
        model.addAttribute("incompletePage", incompletePage);   // 現在ページ番号
        model.addAttribute("completePage", completePage);

        return "todos/todo-list"; // todo-list.html に遷移
    }

    // ────────── 新規作成フォーム表示 ──────────
    @GetMapping("/new")
    public String newTodoForm(@RequestParam(required = false) Long categoryId, Model model) {
        Todo todo = new Todo(); // 新しいTodoオブジェクトを作成

        // カテゴリIDが指定されていた場合はセット
        if (categoryId != null) {
            todoService.getCategory(categoryId).ifPresent(todo::setCategory);
        }

        model.addAttribute("todo", todo);                       // ViewにTodoを渡す
        model.addAttribute("categories", todoService.getAllCategories()); // カテゴリ一覧
        return "todos/todo-new"; // 新規作成フォーム
    }

    // ────────── 新規作成・保存 ──────────
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo,
                             @RequestParam(required = false) String newCategoryName,
                             RedirectAttributes redirectAttributes) {
        todoService.createTodo(todo, newCategoryName);          // Serviceで保存
        redirectAttributes.addFlashAttribute("message", "Todoを作成しました"); // フラッシュメッセージ
        return "redirect:/todos";                               // 一覧にリダイレクト
    }

    // ────────── 編集フォーム表示 ──────────
    @GetMapping("/{id}/edit")
    public String editTodoForm(@PathVariable Long id, Model model) {
        model.addAttribute("todo", todoService.getTodo(id));           // 編集対象をセット
        model.addAttribute("categories", todoService.getAllCategories()); // カテゴリ一覧
        return "todos/todo-edit";                                      // 編集画面
    }

    // ────────── 更新 ──────────
    @PostMapping("/{id}/update")
    public String updateTodo(@PathVariable Long id,
                             @ModelAttribute Todo todo,
                             @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
                             RedirectAttributes redirectAttributes) {
        todoService.updateTodo(id, todo, newCategoryName);             // 更新処理
        redirectAttributes.addFlashAttribute("message", "Todoを更新しました");
        return "redirect:/todos";                                      // 一覧にリダイレクト
    }

    // ────────── 削除 ──────────
    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id);                                     // 削除処理
        redirectAttributes.addFlashAttribute("message", "Todoを削除しました");
        return "redirect:/todos";                                       // 一覧にリダイレクト
    }

    // ────────── 完了トグル ──────────
    @GetMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        todoService.toggleTodo(id);                                     // 完了状態を反転
        redirectAttributes.addFlashAttribute("message", "完了状態を変更しました");
        return "redirect:/todos";                                       // 一覧にリダイレクト
    }

    // ────────── キーワード検索 ──────────
    @GetMapping("/search")
    public String searchTodos(@RequestParam(name = "keyword", required = false) String keyword,
                              Model model) {
        // キーワード検索（未完了/完了別）
        model.addAttribute("incompleteTodos", todoService.searchTodosPage(keyword, false, PageRequest.of(0, 10)));
        model.addAttribute("completeTodos", todoService.searchTodosPage(keyword, true, PageRequest.of(0, 10)));
        model.addAttribute("keyword", keyword); // 入力欄に保持
        model.addAttribute("incompletePage", 0); // 検索後は1ページ目
        model.addAttribute("completePage", 0);
        return "todos/todo-list"; // 一覧画面表示
    }

    // ────────── ソート + 日付絞り込み ──────────
    @GetMapping("/sort")
    public String sortTodos(@RequestParam(required = false) String sortBy,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(defaultValue = "0") int incompletePage,
                            @RequestParam(defaultValue = "0") int completePage,
                            Model model) {

        Pageable incompletePageable = PageRequest.of(incompletePage, 10);
        Pageable completePageable = PageRequest.of(completePage, 10);

        // 日付指定がある場合は日付で絞り込み
        if (date != null) {
            model.addAttribute("incompleteTodos", todoService.getTodosByDatePage(date, false, incompletePageable));
            model.addAttribute("completeTodos", todoService.getTodosByDatePage(date, true, completePageable));
            model.addAttribute("date", date);
        } else {
            // 通常のソート処理
            model.addAttribute("incompleteTodos", todoService.sortTodosPage(sortBy, false, incompletePageable));
            model.addAttribute("completeTodos", todoService.sortTodosPage(sortBy, true, completePageable));
            model.addAttribute("sortBy", sortBy);
        }

        model.addAttribute("incompletePage", incompletePage); // 現在ページ番号
        model.addAttribute("completePage", completePage);

        return "todos/todo-list";
    }
}
