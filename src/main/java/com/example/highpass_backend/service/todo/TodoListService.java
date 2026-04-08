package com.example.highpass_backend.service.todo;

import com.example.highpass_backend.dto.todo.TodoListRequest;
import com.example.highpass_backend.dto.todo.TodoListResponse;
import com.example.highpass_backend.entity.todo.TodoList;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.todo.TodoListRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoListService {
    private final TodoListRepository todolistRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoListResponse createTodo(Long userId, TodoListRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다. ID: " + userId));

        TodoList todoList = TodoList.builder()
                .user(user)
                .content(request.getContent())
                .date(request.getDate())
                .status(request.isStatus())
                .build();

        TodoList savedTodoList = todolistRepository.save(todoList);

        return TodoListResponse.from(savedTodoList);
    }

    @Transactional(readOnly = true)
    public List<TodoListResponse> getTodosByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        return todolistRepository.findAllByUserAndDate(user, date)
                .stream()
                .map(TodoListResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoListResponse toggleStatus(Long id) {
        TodoList todo = todolistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 일이 존재하지 않습니다. ID: " + id));

        todo.setStatus(!todo.isStatus());

        return TodoListResponse.from(todo);
    }

    @Transactional
    public TodoListResponse updateContent(Long todoId, String newContent) {
        TodoList todoList = todolistRepository.findById(todoId).orElseThrow(() -> new RuntimeException("해당 할 일이 존재하지 않습니다."));
        todoList.updateContent(newContent);
        return TodoListResponse.from(todoList);
    }

    @Transactional
    public void deleteTodo(Long id) {
        todolistRepository.deleteById(id);
    }
}


