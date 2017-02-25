package com.example.valhallasoft.getbyid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandoval on 24/02/2017.
 */

public class Result {

    @SerializedName("todos")
    @Expose
    private List<Todo> todos = new ArrayList();

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}
