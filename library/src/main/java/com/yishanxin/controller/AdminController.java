package com.yishanxin.controller;

import com.yishanxin.entity.Admin;
import com.yishanxin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;


@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @RequestMapping("login")
    @ResponseBody
    public HashMap<String, Object> login(String username, String password){
        System.out.println(username);
        HashMap<String, Object> map = new HashMap<>();
        String message;
            try {
                Admin admin = adminService.findByUsername(username);
                if(admin.getPassword().equals(password)){
                        map.put("login",true);
                    }else{
                       message="你输入的密码有误";
                       map.put("message",message);
                       map.put("login",false);
                    }
            } catch (Exception e) {
                message="你输入的用户不存在";
                map.put("message",message);
                map.put("login",false);
                e.printStackTrace();
            }finally {
                return map;
        }
    }
}
