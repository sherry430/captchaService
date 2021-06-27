package demo.service;

import demo.entity.user;
import demo.mapper.userMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserLoginService {
    @Autowired
    private userMapper usermapper;
    //用户登录
    public user userLogin(String username, String password){
        return usermapper.userLogin(username,password);
    }
    //注册新用户
    public int userRegister(String username, String password, String chargeType){
        return usermapper.addUser(username,password,chargeType);
    }


    //修改用户信息
    public user modifyUser(user user) {
        return usermapper.modifyUser(user);
    }


    public user findUserById(user user) {
        return usermapper.findUserById(user);
    }

//    public user findUserByUsername(user user) {
//        return usermapper.findUserByUsername(user);
//    }
}
