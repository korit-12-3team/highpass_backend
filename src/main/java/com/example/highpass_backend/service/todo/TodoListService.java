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
    public Long createTodo(Long userId, TodoListRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userId));

        TodoList todo = TodoList.builder()
                .user(user)
                .content(request.getContent())
                .date(request.getDate())
                .status(request.isStatus())
                .build();

        return todolistRepository.save(todo).getId();
    }

    @Transactional
    public List<TodoListResponse> getTodosByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return todolistRepository.findAllByUserAndDate(user, date)
                .stream()
                .map(TodoListResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleStatus(Long id) {
        TodoList todo = todolistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일이 존재하지 않습니다. ID: " + id));

        todo.setStatus(!todo.isStatus());
    }

    @Transactional
    public void deleteTodo(Long id) {
        todolistRepository.deleteById(id);
    }
}


