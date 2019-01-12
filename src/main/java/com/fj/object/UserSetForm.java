package com.fj.object;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lenovo on 2019/1/3.
 */
public class UserSetForm {
    private Set<User> users;

    public UserSetForm(Set<User> users) {
        this.users = new LinkedHashSet<User>();
        this.users.add(new User());
        this.users.add(new User());

    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    @Override
    public String toString() {
        return "UserSetForm{" +
                "users=" + users +
                '}';
    }
}
