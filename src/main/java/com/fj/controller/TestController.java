package com.fj.controller;

import com.fj.object.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2019/1/2.
 */
@Controller
public class TestController {
    @RequestMapping(value = "baseType.do")
    @ResponseBody
    public String baseType(@RequestParam("xage") int age) {
        return "age: " + age;
    }


    @RequestMapping(value = "baseType2.do")
    @ResponseBody
    public String baseType2(Integer age) {
        return "age: " + age;
    }

    @RequestMapping(value = "array.do")
    @ResponseBody
    public String array(String[] names) {
        StringBuilder sb = new StringBuilder();
        for(String name : names) {
            sb.append(name).append(",");
        }
        return sb.toString();
    }

    @RequestMapping(value = "object.do")
    @ResponseBody
    public String object(User user) {
        return user.toString();
    }

    @RequestMapping(value = "object2.do")
    @ResponseBody
    public String object2(User user, Admin admin) {
        return user.toString() + '\n' + admin.toString();
    }

    @InitBinder("user")
    public void initUser(WebDataBinder webDataBinder) {
        webDataBinder.setFieldDefaultPrefix("user.");
    }

    @InitBinder("admin")
    public void initAdmin(WebDataBinder webDataBinder) {
        webDataBinder.setFieldDefaultPrefix("admin.");
    }

    // 请求的最好是连续的，否则会出现内存浪费的情况
    @RequestMapping(value = "list.do")
    @ResponseBody
    public String list(UserListForm userListForm) {
        return userListForm.toString();
    }

    @RequestMapping(value = "set.do")
    @ResponseBody
    public String set(UserSetForm userSetForm) {
        return userSetForm.toString();
    }

    @RequestMapping(value = "map.do")
    @ResponseBody
    public String map(UserMapForm userMapForm) {
        return userMapForm.toString();
    }

    @RequestMapping(value = "json.do")
    @ResponseBody
    public String json(@RequestBody User user) {
        return user.toString();
    }

    @RequestMapping(value = "xml.do")
    @ResponseBody
    public String xml(@RequestBody Admin admin) {
        return admin.toString();
    }


    @RequestMapping(value = "date1.do")
    @ResponseBody
    public String date1(Date date1) {
        return date1.toString();
    }

    @InitBinder("data1")
    public void initDate(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true));
    }

    @RequestMapping(value = "/book",method = RequestMethod.GET)
    @ResponseBody
    public String book(HttpServletRequest request) {
        String contentType = request.getContentType();
        if(contentType == null)
            return "book.default";
        else  if(contentType.equalsIgnoreCase("txt")) {
            return "book.txt";
        } else if(contentType.equalsIgnoreCase("html")) {
            return "book.html";
        }
        return "book.default";
    }

    @RequestMapping(value = "/subject/{subjectId}",method = RequestMethod.GET)
    @ResponseBody
    public String subjectGet(@PathVariable("subjectId") String subjectId) {
        return "this is a get method, subjectId:" + subjectId;
    }

    @RequestMapping(value = "/subject/{subjectId}",method = RequestMethod.POST)
    @ResponseBody
    public String subjectPost(@PathVariable("subjectId") String subjectId) {
        return "this is a post method, subjectId:" + subjectId;
    }


    @RequestMapping(value = "/subject/{subjectId}",method = RequestMethod.DELETE)
    @ResponseBody
    public String subjectDelete(@PathVariable("subjectId") String subjectId) {
        return "this is a delete method, subjectId:" + subjectId;
    }

    @RequestMapping(value = "/subject/{subjectId}",method = RequestMethod.PUT)
    @ResponseBody
    public String subjectPut(@PathVariable("subjectId") String subjectId) {
        return "this is a put method, subjectId:" + subjectId;
    }

}
