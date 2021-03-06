package team.nine.booknutsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import team.nine.booknutsbackend.config.JwtTokenProvider;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.dto.Request.UserRequest;
import team.nine.booknutsbackend.exception.user.PasswordErrorException;
import team.nine.booknutsbackend.service.AuthService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestBody @Valid UserRequest user) {
        User newUser = userService.join(UserRequest.newUser(user, passwordEncoder.encode(user.getPassword())));
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    //유저 닉네임 중복 체크
    @GetMapping("/checkNickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplication(nickname));
    }

    //유저 로그인 아이디 중복 체크
    @GetMapping("/checkLoginId/{loginId}")
    public ResponseEntity<Boolean> checkUserIdDuplicate(@PathVariable String loginId) {
        return ResponseEntity.ok(userService.checkLoginIdDuplication(loginId));
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserRequest user) {
        User loginUser = userService.loadUserByUsername(user.getEmail());
        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            throw new PasswordErrorException("잘못된 비밀번호입니다.");
        }

        /*
        * refresh 토큰 구현 전,
        * 로그인할 때마다 token 새로 생성
        */
        String token = jwtTokenProvider.createToken(loginUser.getUsername(), loginUser.getRoles()); //getUsername -> 이메일 반환
        userService.updateToken(loginUser.getUserId(), token);

        Map<String, String> map = new LinkedHashMap<>();
        map.put("nickname", loginUser.getNickname());
        map.put("token", token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //현재 유저 정보 - 헤더 토큰으로 조회
    @GetMapping("/userinfo")
    public ResponseEntity<Object> userInfoByHeaderToken(Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}