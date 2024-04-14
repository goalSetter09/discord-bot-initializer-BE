package hongik.discordbots.initializer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<Object> testApi() {
        String result = "API 통신에 성공하였습니다.";
        System.out.println("result = " + result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("")
    public String hello() {
        return "hello";
    }
}