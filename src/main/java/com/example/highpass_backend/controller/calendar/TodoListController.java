package com.example.highpass_backend.controller.calendar;

import com.example.highpass_backend.dto.calendar.TodoListRequest;
import com.example.highpass_backend.dto.calendar.TodoListResponse;
import com.example.highpass_backend.service.calendar.TodoListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<TodoListResponse>> getList(@PathVariable Long userId) {
        List<TodoListResponse> responses = todoListService.getTodosByDate(userId);
        return ResponseEntity.ok(responses);
    }

    // 완료 상태 변경
    @PatchMapping("/{todoId}/status")
    public ResponseEntity<TodoListResponse> done(@PathVariable("todoId") Long todoId) {
        TodoListResponse updateTodo = todoListService.toggleStatus(todoId);
        return ResponseEntity.ok(updateTodo);
    }

    // 내용 수정
    @PatchMapping("/{todoId}/content")
    public ResponseEntity<TodoListResponse> updateContent(@PathVariable("todoId") Long todoId, @RequestBody TodoListRequest request) {
        TodoListResponse updatedTodo = todoListService.updateContent(todoId, request.getContent());
        return ResponseEntity.ok(updatedTodo);
    }

    // 할 일 삭제
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable("todoId") Long todoId) {
        todoListService.deleteTodo(todoId);
        return ResponseEntity.ok().build();
    }
}
