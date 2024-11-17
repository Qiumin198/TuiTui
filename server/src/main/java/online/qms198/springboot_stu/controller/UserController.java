package online.qms198.springboot_stu.controller;

//import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import online.qms198.springboot_stu.constants.MyConstant;
import online.qms198.springboot_stu.pojo.ResponseMessage;
import online.qms198.springboot_stu.pojo.User;
import online.qms198.springboot_stu.pojo.dto.UserLoginDto;
import online.qms198.springboot_stu.pojo.dto.UserRegisterDto;
import online.qms198.springboot_stu.service.IUserService;
import online.qms198.springboot_stu.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;

@RestController // 标记请求处理类。接口方法返回对象，转换成json文本
@RequestMapping("/user") // localhost:8088/user/，标记拦截user URL前缀地址类
@CrossOrigin(origins = "http://localhost:8080/login")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);//日志记录对象
    @Autowired
    IUserService userService;

    // 增加
    // URL: localhost:8088/user
    @PostMapping("/register")
    public ResponseMessage<User> add(@Valid @Validated @RequestBody UserRegisterDto user) {
        User userNew = userService.add(user);
        return ResponseMessage.success(userNew);
    }

//    @PostMapping("/login")
//    public ResponseMessage<User> login(@Valid @RequestBody UserLoginDto userLoginDto) {
//        logger.info("Attempting login for user: {}", userLoginDto.getUserAccount());
//        User authenticatedUser = userService.authenticate(userLoginDto.getUserAccount(), userLoginDto.getPassword());
//        if (authenticatedUser != null) {
//            return ResponseMessage.success(authenticatedUser);
//        } else {
//            return new ResponseMessage<>(401, "Invalid username or password", null);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<User>> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        logger.info("Attempting login for user: {}", userLoginDto.getUserAccount());
        User authenticatedUser = userService.authenticate(userLoginDto.getUserAccount(), userLoginDto.getPassword());

        if (authenticatedUser != null) {
            String token = JwtUtil.createToken(authenticatedUser.getUserAccount());

            logger.info("Generated JWT token: {}", token);
            // 创建响应消息
            ResponseMessage<User> responseMessage = ResponseMessage.success(authenticatedUser);

            // 返回带有 token 的响应头
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body(responseMessage);
        } else {
            ResponseMessage<User> responseMessage = new ResponseMessage<>(401, "Invalid username or password", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
        }
    }


    // 查询
    @GetMapping("/{userAccount}") // URL: localhost:8088/user/userId method: delete
    public ResponseMessage<User> get(@PathVariable String userAccount) {
        User userNew = userService.getUserByUserAccount(userAccount);
        return ResponseMessage.success(userNew);
    }

    // 修改
    @PutMapping
    public ResponseMessage<User> edit(@Validated @RequestBody UserRegisterDto user) {
        User userNew = userService.edit(user);
        return ResponseMessage.success(userNew);
    }

    // 删除
    @DeleteMapping("/{userId}") // URL: localhost:8088/user/userId method: delete
    public ResponseMessage<User> delete(@PathVariable Integer userId) {
        userService.delete(userId);
        return ResponseMessage.success();
    }
}