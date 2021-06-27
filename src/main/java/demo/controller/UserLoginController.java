package demo.controller;

import demo.entity.user;
//import demo.redis.JedisCreate;
import demo.service.UserLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = {"/user"})
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

//    private Jedis jd = JedisCreate.createJedis("127.0.0.1", 6379);

    //主页
    @RequestMapping(value = {"/homepage"})
    public String homepage() {
        return "homepage";
    }

    //获取用户姓名，显示在主页上
    @ResponseBody
    @RequestMapping(value = {"/showName"})
    public String showName(HttpServletRequest request){
        String user = (String) request.getSession().getAttribute("session");
        System.out.println("user:" + user + "has logged in!");
        if(user == null){
            user = "loginOut";
        }
        return user;
    }

    //退出登录
    @ResponseBody
    @RequestMapping(value = {"/loginOut"})
    public String loginOut(HttpServletRequest request) {
        String res = "fail";
        request.getSession().removeAttribute("session");
        if(request.getSession().getAttribute("session") == null){
            System.out.println("user has logged out!");
            res = "success";
        }else{
            System.out.println("fail to delete session and log out!");
        }
        return res;
    }

    //显示个人信息
    @RequestMapping(value = {"/showInfo"})
    public String showInfo() {
        return "showInfo";
    }

    //显示用户信息响应
    @ResponseBody
    @RequestMapping(value = {"/getInfo"})
    public String getInfo(HttpServletRequest request){
        String user = (String) request.getSession().getAttribute("session");
        System.out.println("get session:" + user );
        return user;
    }

    //修改个人信息
    @RequestMapping(value = {"/changeInfo"})
    public String changeInfo() {
        return "changeInfo";
    }

    //修改个人信息响应
    @ResponseBody
    @RequestMapping(value = "/handleInfo")
    public void putUser(user updateUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String userInfo = (String) request.getSession().getAttribute("session");
        ObjectMapper objectMapper = new ObjectMapper();
        user userInfomation = objectMapper.readValue(userInfo,user.class);
        System.out.println("userInfo" + userInfomation.getId());
        updateUser.setId(userInfomation.getId());
        System.out.println("the id :" + updateUser.getId());
        System.out.println("the chargeType :" + updateUser.getChargeType());
        userLoginService.modifyUser(updateUser);
        user user = userLoginService.findUserById(updateUser);
        if(user == null){
            System.out.println("user=null");
        }else {
            request.getSession().removeAttribute("session");
            ObjectMapper mapperUser = new ObjectMapper();
            String json = mapperUser.writeValueAsString(user);
            System.out.println("new json " + json);
            request.getSession().setAttribute("session",json);
            }
        }

    //登录页面
    @RequestMapping(value = {"/login"})
    public String userLogin() {
        return "login";
    }

    //登录页面响应
    @ResponseBody
    @RequestMapping(value = {"/loginResult"})
    public String loginHandler(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request) throws JsonProcessingException {
        String res = "<html><body><a href=\"/user/login\">登录失败，请重新输入</a></body></html>";

        if (StringUtils.isEmpty(username)) {
            res =  "用户名不能为空";
        }
        if (StringUtils.isEmpty(password)) {
            res =  "密码不能为空";
        }

        user user = userLoginService.userLogin(username, password);
        if (user != null) {                                                  //登录成功
//                jd.set(username, password);  //用户信息存入redis
//                System.out.println("jd存储密码" + jd.get(username));
            ObjectMapper mapperUser = new ObjectMapper();
            String json = mapperUser.writeValueAsString(user);
            System.out.println("try json " + json);
            request.getSession().setAttribute("session", json);
            res =  "<html><head><script>window.location.href = \"/user/homepage\";</script></head></html>";
        }
        return res;

//        if (jd.get(username) != null) {
//            System.out.println("有jedis存在：" + jd.get(username));
//            if (jd.get(username).equals(password)) {
//                System.out.println("jedis验证成功");
//                user user = new user();
//                user.setUsername(username);
//                user = userLoginService.findUserByUsername(user);
//
//                ObjectMapper mapperUser = new ObjectMapper();
//                String json = mapperUser.writeValueAsString(user);
//                System.out.println("try json " + json);
//                request.getSession().setAttribute("session", json);
//                res =  "<html><head><script>window.location.href = \"/user/homepage\";</script></head></html>";
//            }
//        } else {
//            System.out.println("没有redis缓存");
//            user user = userLoginService.userLogin(username, password);
//            if (user != null) {                                                  //登录成功
//                jd.set(username, password);  //用户信息存入redis
//                System.out.println("jd存储密码" + jd.get(username));
//                ObjectMapper mapperUser = new ObjectMapper();
//                String json = mapperUser.writeValueAsString(user);
//                System.out.println("try json " + json);
//                request.getSession().setAttribute("session", json);
//                res =  "<html><head><script>window.location.href = \"/user/homepage\";</script></head></html>";
//            }
//        }
//        return res;
    }

    //注册页面
    @RequestMapping(value = {"/register"})
    public String userRegister() {
        return "register";
    }


    //注册页面响应
    @ResponseBody
    @RequestMapping(value = {"/registerResult"})
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("password2") String password2,
                          @RequestParam("chargeType") String chargeType){

        if(StringUtils.isEmpty(username)){
            return "用户名不能为空";
        }

        if(StringUtils.isEmpty(password)){
            return "密码不能为空";
        }

        if(StringUtils.isEmpty(password2)){
            return "确认密码不能为空";
        }

        if(!password.equals(password2)){
            return "两次密码不相同，注册失败！！";
        }else {
            int res = userLoginService.userRegister(username,password,chargeType);
            if(res == 0){
                return "<html><body><a href=\"/user/register\">注册失败，用户名已存在</a></body></html>";
            }else {
                return "<html><body><a href=\"/user/login\">注册成功，前去登录</a></body></html>";
            }
        }

    }

}
