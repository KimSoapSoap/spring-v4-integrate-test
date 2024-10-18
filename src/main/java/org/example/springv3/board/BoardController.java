package org.example.springv3.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springv3.core.util.Resp;
import org.example.springv3.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardService boardService;

    // localhost:8080?title=제목
    @GetMapping("/")
    public ResponseEntity<?> list(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        BoardResponse.PageDTO pageDTO = boardService.게시글목록보기(title, page);
        return ResponseEntity.ok(pageDTO);
    }


    @DeleteMapping("/api/board/{id}")
    public ResponseEntity<?> removeBoard(@PathVariable("id") Integer id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.게시글삭제하기(id, sessionUser);
        return ResponseEntity.ok(Resp.ok(null));
    }


    @PostMapping("/api/board")
    public ResponseEntity<?> save(@Valid @RequestBody BoardRequest.SaveDTO saveDTO, Errors errors) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DTO model = boardService.게시글쓰기(saveDTO, sessionUser);
        return ResponseEntity.ok(Resp.ok(model));
    }

    // 수정이니까 PutMapping으로 하고 주소에 update 동사 삭제
    @PutMapping("/api/board/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @Valid BoardRequest.UpdateDTO updateDTO, Errors errors) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DTO model = boardService.게시글수정(id, updateDTO, sessionUser);
        return ResponseEntity.ok(Resp.ok(model));
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DetailDTO model = boardService.게시글상세보기(sessionUser, id);
        return ResponseEntity.ok(Resp.ok(model));
    }
}
