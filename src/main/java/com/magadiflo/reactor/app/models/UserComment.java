package com.magadiflo.reactor.app.models;

public class UserComment {
    private User user;
    private Comment comment;

    public UserComment(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserComment{");
        sb.append("user=").append(user);
        sb.append(", comment=").append(comment);
        sb.append('}');
        return sb.toString();
    }
}
