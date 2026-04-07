package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.todo.TodoListRequest;
import com.example.highpass_backend.dto.todo.TodoListResponse;
import com.example.highpass_backend.service.TodoListService;
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
    public ResponseEntity<Long> create(@PathVariable Long userId, @RequestBody TodoListRequest request) {
        Long todoId = todoListService.createTodo(userId, request);
        return ResponseEntity.ok(todoId);
    }

    // 날짜별 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<TodoListResponse>> getList(
            @PathVariable Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
        List<TodoListResponse> responses = todoListService.getTodosByDate(userId, date);
        return ResponseEntity.ok(responses);
    }

    // 완료 상태
    @PatchMapping("/status/{id}")
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