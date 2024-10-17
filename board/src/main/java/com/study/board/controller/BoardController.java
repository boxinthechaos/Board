package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.exception.FileNotPresentException; // 예외 임포트
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") // localhost:8080/board/write
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new FileNotPresentException("파일이 존재하지 않습니다.");
        }
        boardService.write(board, file);

        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable , String searchKeyword) {

        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardview(id); // 기존 글을 조회

        // 제목과 내용을 업데이트
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        // 파일이 null인지 확인 후 처리
        if (file != null && !file.isEmpty()) {
            boardService.write(boardTemp, file); // 파일이 있을 때만 파일 처리
        } else {
            boardService.updateWithoutFile(boardTemp); // 파일이 없을 경우 별도의 업데이트 로직
        }

        return "redirect:/board/list"; // list로 경로 수정
    }


}
