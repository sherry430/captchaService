package demo.service;

import demo.entity.user;
import demo.mapper.userMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
public class CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private final userMapper usermapper;

    private static final Duration TOKEN_EXPIRE_TIME = Duration.ofSeconds(3600);

    public CaptchaService(StringRedisTemplate stringRedisTemplate, userMapper usermapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.usermapper = usermapper;
    }

    // 保存session id对应的验证码
    public void saveStringCaptcha(String sid, String captcha) {
        stringRedisTemplate.opsForValue().set(sid, captcha, Duration.ofSeconds(180));
    }

    // 获取验证码真值
    public String getRealStringCaptcha(String sid) {
        return stringRedisTemplate.opsForValue().get(sid);
    }

    // 删除验证码记录
    public void disableCaptcha(String sid) {
        stringRedisTemplate.delete(sid);
    }

    // 验证客户登录信息
    public boolean validateUser(String username, String password) {
        /* 访问数据库获取用户信息 */
//        String realAccessId = "test";
//        String realAccessSecret = "test";
        return usermapper.userLogin(username, password) != null;
    }

    // 验证token
    public boolean validateUser(String token) {
        Boolean result = stringRedisTemplate.hasKey(token);
        if (result != null && result.equals(true)) {
            stringRedisTemplate.expire(token, TOKEN_EXPIRE_TIME);
            return true;
        } else {
            return false;
        }
    }

    // 保存token一小时
    public void saveToken(String token, String username) {
        stringRedisTemplate.opsForValue().set(token, username, TOKEN_EXPIRE_TIME);
    }

    public void addCount(String token) {
        String username = stringRedisTemplate.opsForValue().get(token);
        /* 添加使用次数 */
        usermapper.count(username);
    }
}
