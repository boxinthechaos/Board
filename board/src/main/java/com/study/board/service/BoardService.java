package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception {

        // 파일이 null이 아니고 비어 있지 않은 경우에만 파일 저장 처리
        if (file != null && !file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "//src//main//resources//static//files";

            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            // 파일 이름과 경로를 게시글 엔티티에 설정
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        // 파일이 없을 경우에는 파일 이름과 경로 설정 없이 저장
        boardRepository.save(board);
    }

    // 게시물 리스트 처리
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){

        return  boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시물 불러오기
    public Board boardview(Integer id) {
        return boardRepository.findById(id).orElse(null);
    }

    // 특정 게시물 삭제
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    public void updateWithoutFile(Board board) {
        boardRepository.save(board); // 파일 없이 기존 데이터만 저장
    }
}
