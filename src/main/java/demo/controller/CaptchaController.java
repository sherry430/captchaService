package demo.controller;

import demo.service.CaptchaService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
public class CaptchaController {

    private final DefaultKaptcha kaptchaProducer;
    private final CaptchaService captchaService;

    public CaptchaController(DefaultKaptcha kaptchaProducer, CaptchaService captchaService) {
        this.kaptchaProducer = kaptchaProducer;
        this.captchaService = captchaService;
    }

    @GetMapping("/api/captcha")
    public String getCaptcha(HttpServletRequest request) throws IOException {

        String sid = request.getSession().getId();
        String captcha = kaptchaProducer.createText();
        captchaService.saveStringCaptcha(sid, captcha);

        BufferedImage image = kaptchaProducer.createImage(captcha);

//        ServletOutputStream outputStream = response.getOutputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        return captcha + ",data:image/png;base64," + Base64.encodeBase64String(outputStream.toByteArray());
    }

    @GetMapping("/api/validator")
    public ValidResponse validateCaptcha(
            @RequestParam String sid,
            @RequestParam String captcha,
            @RequestParam String token
    ) {
        if (!captchaService.validateUser(token)) {
            return new ValidResponse(false, "invalid token");
        }
        /* +1 */
        String realCaptcha = captchaService.getRealStringCaptcha(sid);
        if (realCaptcha == null) {
            return new ValidResponse(false, "invalid sid");
        }
        captchaService.disableCaptcha(sid);
        if (captcha.equals(realCaptcha)) {
            return new ValidResponse(true, "success");
        } else {
            return new ValidResponse(false, "wrong captcha");
        }
    }

    @GetMapping("/api/token")
    public ValidResponse getToken(@RequestParam String username, @RequestParam String password) {
        if (captchaService.validateUser(username, password)) {
            String token = UUID.randomUUID().toString();
            captchaService.saveToken(token, username);
            return new ValidResponse(true, token);
        } else {
            return new ValidResponse(false, "login failed");
        }
    }
}

@Data
@AllArgsConstructor
class ValidResponse {
    private boolean success;
    private String data;
}
