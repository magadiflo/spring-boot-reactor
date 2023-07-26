package com.magadiflo.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private List<String> comentarios;

    public Comment() {
        this.comentarios = new ArrayList<>();
    }

    public void addComment(String comment) {
        this.comentarios.add(comment);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Comment{");
        sb.append("comentarios=").append(comentarios);
        sb.append('}');
        return sb.toString();
    }
}
