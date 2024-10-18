package org.example.springv3.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springv3.core.util.Resp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final HttpSession session;
    private final UserService userService;

    // 업데이트 하는 거니까!!
    @PutMapping("/api/user/profile")
    public ResponseEntity<?> profile(@RequestParam("profile") MultipartFile profile){
        User sessionUser = (User) session.getAttribute("sessionUser");
        UserResponse.DTO model = userService.프로필업로드(profile, sessionUser);
        return ResponseEntity.ok(Resp.ok(model));
    }

    // profile-form 에서 form 삭제!! 화면을 가져오는게 아니니까!!
    @GetMapping("/api/user/profile")
    public ResponseEntity<?> profileForm() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        String model = userService.프로필사진가져오기(sessionUser);
        return ResponseEntity.ok(Resp.ok(model));
    }

    // http://localhost:8080/user/samecheck?username=hello
    @GetMapping("/user/samecheck")
    public ResponseEntity<?> sameCheck(@RequestParam("username") String username) {
        boolean isSameUsername = userService.유저네임중복되었니(username);
        return ResponseEntity.ok(Resp.ok(isSameUsername, isSameUsername ? "중복되었어요" : "중복되지않았어요"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.LoginDTO loginDTO, Errors errors) {
        String accessToken = userService.로그인(loginDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer "+accessToken)
                .body(Resp.ok(null));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserRequest.JoinDTO joinDTO, Errors errors) {
        UserResponse.DTO model = userService.회원가입(joinDTO);
        return ResponseEntity.ok(Resp.ok(model));
    }

}
