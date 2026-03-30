package com.example.highpass_backend.entity.certificate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
public class Certificate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}