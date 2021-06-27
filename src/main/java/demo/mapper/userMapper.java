package demo.mapper;

import demo.entity.user;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface userMapper {

    //用户登录
    user userLogin(@Param("username") String username, @Param("password") String password);

    //注册新用户
    int addUser(@Param("username") String username, @Param("password") String password,@Param("chargeType") String chargeType);

    //修改用户信息
    user modifyUser(user user);

    //修改调用次数
    void count(String username);

    //ID查询用户信息
    user findUserById(user user);

    //用户名查询用户信息
//    user findUserByUsername(user user);

}