package com.transport.tracking.k.controller;

import com.transport.tracking.k.annotation.Anonymous;
import com.transport.tracking.k.service.UserService;
import com.transport.tracking.response.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins={"http://localhost:3000","http://localhost:8052","http://localhost:8051","http://172.16.0.4:8051","http://172.16.0.4:8052","http://csrouteplanner.kerrcloud.com:8051","http://csrouteplanner.kerrcloud.com:8052","https://csrouteplanner.kerrcloud.com:8051","https://csrouteplanner.kerrcloud.com:8052"})
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping ("/login")
    @Anonymous
    public @ResponseBody ResponseEntity<Object> login(@RequestBody UserVO userVO, HttpServletResponse response) {
        log.info("UserVO  ======== ", userVO);
        userVO = userService.login(userVO, response);
        if(Objects.isNull(userVO)) {            Map<String, String> map = new HashMap<>();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map.put("message", "error"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userVO);
    }

    @GetMapping ("/logout")
    @Anonymous
    public @ResponseBody ResponseEntity<Object> logout(HttpServletResponse response) {
        userService.logOut(response);
        return ResponseEntity.status(HttpStatus.OK).body("sucess");
    }
}
