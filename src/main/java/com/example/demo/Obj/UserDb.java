package com.example.demo.Obj;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class UserDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDb(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDb(String name) {
        this.name = name;
    }

    public UserDb() {
    }
}
