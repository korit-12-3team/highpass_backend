package com.example.highpass_backend.controller.todo;

import com.example.highpass_backend.dto.todo.TodoListRequest;
import com.example.highpass_backend.dto.todo.TodoListResponse;
<<<<<<< Updated upstream:src/main/java/com/example/highpass_backend/controller/todo/TodoListController.java
import com.example.highpass_backend.service.todo.TodoListService;
=======
import com.example.highpass_backend.service.TodoListService;
import org.springframework.http.HttpStatus;
>>>>>>> Stashed changes:src/main/java/com/example/highpass_backend/controller/TodoListController.java
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoListController {
    private final TodoListService todoListService;

    // 할 일 등록
    @PostMapping("/{userId}")
    public ResponseEntity<TodoListResponse> createTodoList(@PathVariable Long userId, @RequestBody TodoListRequest request) {
        TodoListResponse todoListResponse = todoListService.createTodo(userId, request);
        return new ResponseEntity<>(todoListResponse,HttpStatus.CREATED);
    }

    // 날짜별 목록 조회
    @GetMapping("/{id}")
    public ResponseEntity<List<TodoListResponse>> getList(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
        List<TodoListResponse> responses = todoListService.getTodosByDate(id, date);
        return ResponseEntity.ok(responses);
    }

    // 완료 상태
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> done(@PathVariable("id") Long id) {
        todoListService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    // 할 일 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoListService.deleteTodo(id);
        return ResponseEntity.ok().build();
    }
}