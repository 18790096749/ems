package com.yishanxin.controller;

import com.yishanxin.entity.Category;
import com.yishanxin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("findAll")
    @ResponseBody
    public Map<String,Object> queryAll(Integer page, Integer rows){
        HashMap<String, Object> result = new HashMap<>();
        //获取总条数
        long totals = categoryService.findAll().size();
        //准备当前页
        result.put("page", page);
        Long totalPage = totals % rows == 0 ? totals / rows : totals / rows + 1;
        result.put("total", totalPage);//总页数
        result.put("records", totals);//总记录数
        List<Category> cates = categoryService.queryPage(page, rows);
        result.put("rows",cates);
        return result;
    }
    @RequestMapping("findFirst")
    @ResponseBody
    public List<Category> queryFirst(){
        List<Category> allFirst = categoryService.findAllFirst();
        return allFirst;
    }
    @RequestMapping("add")
    @ResponseBody
    public void add(Category category) {
       // System.out.println(category);
        if(category.getParentId()!=null){
            category.setLevels(2);
        }else{
            category.setLevels(1);
        }
        //category.setCategory(null);
        categoryService.add(category);
    }
    @RequestMapping("delete")
    @ResponseBody
    public HashMap<String, Object> delete(String id){
        HashMap<String, Object> map = new HashMap<>();
        try {
            System.out.println(id);
            Category category = categoryService.queryById(id);
            Integer levels = category.getLevels();
            String parent_id = category.getParentId();
            if(levels.equals("2")){
                categoryService.removeById(id);
            }else{
                categoryService.removeByParent_id(parent_id);
                categoryService.removeById(id);
            }
            map.put("message",true);
        }catch (Exception e){
            e.printStackTrace();
            map.put("message",false);
        }finally {
            return map;
        }
    }
}
