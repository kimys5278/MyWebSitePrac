package com.springboot.godwebsite.controller;

import com.springboot.godwebsite.entity.Board;
import com.springboot.godwebsite.entity.Comment;
import com.springboot.godwebsite.repository.BoardRepository;
import com.springboot.godwebsite.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardApiController {

    @Autowired
    private BoardRepository repository;

    @Autowired
    private CommentRepository commentRepository;

    // Aggregate root

    @GetMapping("/boards")
    public List<Board> all(@RequestParam(required = false, defaultValue = "") String title,
                           @RequestParam(required = false, defaultValue = "") String content) {
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
            return repository.findAll();
        } else {
            return repository.findByTitleOrContent(title, content);
        }
    }

    @PostMapping("/boards")
    public Board newBoard(@RequestBody Board newBoard) {
        return repository.save(newBoard);
    }

    // Single item

    @GetMapping("/boards/{id}")
    public Board one(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/boards/{id}")
    public Board replaceBoard(@RequestBody Board newBoard, @PathVariable Long id) {
        return repository.findById(id)
                .map(board -> {
                    board.setTitle(newBoard.getTitle());
                    board.setContent(newBoard.getContent());
                    return repository.save(board);
                })
                .orElseGet(() -> {
                    newBoard.setId(id);
                    return repository.save(newBoard);
                });
    }

    @DeleteMapping("/boards/{id}")
    public void deleteBoard(@PathVariable Long id) {
        repository.deleteById(id);
    }


    @GetMapping("/boards/{boardId}/comments")
    public List<Comment> getComments(@PathVariable Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    @PostMapping("/boards/{boardId}/comments")
    public Comment addComment(@PathVariable Long boardId, @RequestBody Comment comment) {
        Board board = repository.findById(boardId).orElse(null);
        if (board != null) {
            comment.setBoard(board);
            return commentRepository.save(comment);
        }
        return null;
    }

}
