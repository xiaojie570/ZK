package com.fj.controller;

import com.fj.object.User;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;

/**
 * Created by lenovo on 2019/1/5.
 */
public class MyPropertyEditor extends PropertyEditorSupport{
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        User user = new User();
        String[] textArray = text.split(",");
        user.setName(textArray[0]);
        user.setAge(Integer.parseInt(textArray[1]));
        this.setValue(user);
    }

    public static void main(String[] args) {
        MyPropertyEditor myPropertyEditor = new MyPropertyEditor();
        myPropertyEditor.setAsText("fj,22");
        System.out.println(myPropertyEditor.getValue());
    }
}
