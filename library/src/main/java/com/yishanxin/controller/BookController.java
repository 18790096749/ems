package com.yishanxin.controller;

import com.yishanxin.entity.Book;
import com.yishanxin.entity.Category;
import com.yishanxin.service.BookService;
import com.alibaba.druid.util.StringUtils;
import com.yishanxin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("book")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("findAll")
    @ResponseBody
    public HashMap<String, Object> findAll(Integer page, Integer rows) {
        HashMap<String, Object> result = new HashMap<>();
        //获取总条数
        List<Book> all = bookService.queryAll();
        long totals = all.size();
        //准备当前页
        result.put("page", page);
        Long totalPage = totals % rows == 0 ? totals / rows : totals / rows + 1;
        result.put("total", totalPage);//总页数
        result.put("records", totals);//总记录数
        List<Book> books = bookService.queryPage(page, rows);
        result.put("rows",books);
        return result;

    }
    @RequestMapping("up")
    @ResponseBody
    public void add(Book book, MultipartFile aaa, HttpServletRequest request, String hiddenId, String cover) throws IOException {
        if(StringUtils.isEmpty(hiddenId)){
            System.out.println(book);
            String type = aaa.getOriginalFilename().substring(aaa.getOriginalFilename().indexOf("."));
            String realPath=request.getSession().getServletContext().getRealPath("/upload/");
            String fileName = String.valueOf(System.currentTimeMillis()) + type;
            aaa.transferTo(new File(realPath,fileName));
            book.setCover(fileName);
            book.setCreateDate(new Date());
            bookService.add(book);
        }else {
            //更新
           /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(hiddenTime);*/
            /* System.out.println("进入更新模块"+hiddenId);*/
            book.setId(hiddenId);
            book.setCover(cover);
            book.setCreateDate(new Date());
            bookService.update(book);
        }
    }
    @RequestMapping("delete")
    @ResponseBody
    public HashMap<String, Object> delete(String id,HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<>();
        try {
            Book book = bookService.queryById(id);
            String realPath = request.getServletContext().getRealPath("/upload/");
            File file = new File(realPath,book.getCover());
            file.delete();
            //删除数据库中的数据
            bookService.deleteById(id);
            map.put("message",true);
        }catch (Exception e){
            e.printStackTrace();
            map.put("message",false);
        }finally {
            return map;
        }
    }

    @RequestMapping("showone")
    @ResponseBody
    public Book findOne(String id){
        Book book = bookService.queryById(id);
        //System.out.println(book);
        return book;

    }

    @RequestMapping("findSencond")
    @ResponseBody
    public List<Category> findTwo(){
        List<Category> allSecond = categoryService.findAllSecond();
        return  allSecond;

    }
    @RequestMapping("mainPage")
    //首页
    public String mainPage(HttpServletRequest request){
        //查询所有一级分类（该分类下包括所有二级分类）
        List<Category> allFirst = categoryService.findAllFirst();
        List<Book> list1 = bookService.queryRecommend();
        //查询销量前8的图书
        List<Book> list2 = bookService.queryBySale();
        //查询最新上架的图书
        List<Book> list3 = bookService.queryByCreateDate();
        //查询新书热卖的10个数据
        List<Book> list4 = bookService.queryByNewAndCreateDate();
        request.setAttribute("list1",list1);
        request.setAttribute("list2",list2);
        request.setAttribute("list3",list3);
        request.setAttribute("list4",list4);
        request.setAttribute("allFirst",allFirst);
        System.out.println("进入weiye~~~");
        return "front/main/main";
    }
    @RequestMapping("secondPage")
    //二级页面
    public String secondPage(String fid,String sid,HttpServletRequest request){
        List<Book> list = bookService.queryByfid(fid, sid);
        Category category = categoryService.queryCategoryByFid(fid);
        request.setAttribute("list",list);
        request.setAttribute("category",category);
        return "front/main/book_list";
    }
    @RequestMapping("secondPage2")
    public String secondPage2(String id,HttpServletRequest request){
        //根据图书id查询一条数据
        Book book = bookService.queryById(id);
        request.setAttribute("book",book);
        return "front/main/book_detail";
    }
    @RequestMapping("fenYe")
    //分页
    public String fenYe(Integer page,String fid,String sid,HttpServletRequest request){
        System.out.println(page);
        System.out.println(fid);
        System.out.println(sid);
        Category category = categoryService.queryCategoryByFid(fid);
        if(page==null){
            page=1;
        }
        Integer total;

        List<Book> queryByfid = bookService.queryByfid(fid, sid);
        if(queryByfid.size()%3==0){
            total=queryByfid.size()/3;
        }else{
            total=queryByfid.size()/3+1;
        }
        List<Book> list6 = bookService.selectPage(fid, sid, page);
        System.out.println(total);
        request.setAttribute("total",total);
        request.setAttribute("list6",list6);
        request.setAttribute("category",category);
        request.setAttribute("page",page);
        return "front/main/book_list";
    }

}
