package com.example.highpass_backend.dto.todo;

import com.example.highpass_backend.entity.todo.TodoList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoListResponse {
        private Long id;
        private String content;
        private LocalDate date;
        private boolean status;

        public static TodoListResponse from(TodoList todo) {
                return TodoListResponse.builder()
                        .id(todo.getId())
                        .content(todo.getContent())
                        .date(todo.getDate())
                        .status(todo.isStatus())
                        .build();
        }
}
