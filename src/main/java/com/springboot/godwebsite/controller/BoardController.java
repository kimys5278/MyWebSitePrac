package com.springboot.godwebsite.controller;

import com.springboot.godwebsite.Validator.BoardValidator;
import com.springboot.godwebsite.entity.Board;
import com.springboot.godwebsite.entity.Comment;
import com.springboot.godwebsite.entity.User;
import com.springboot.godwebsite.repository.BoardRepository;
import com.springboot.godwebsite.repository.CommentRepository;
import com.springboot.godwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardValidator boardValidator;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 5)  Pageable pageable,
                       @RequestParam(required = false, defaultValue = "") String  searchText) {
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
        int startPage = Math.max(0, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);

        return "board/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewCount(board.getViewCount() + 1);
            boardRepository.save(board);
            model.addAttribute("board", board);
        }
        return "board/detail";
    }

    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id) {
        if (id == null) {
            model.addAttribute("board", new Board());
        } else {
            Board board = boardRepository.findById(id).orElse(null);
            model.addAttribute("board", board);
        }
        return "board/form";
    }

    @PostMapping("/form")
    public String formSubmit(@Valid Board board, BindingResult bindingResult) {
        boardValidator.validate(board, bindingResult);
        if (bindingResult.hasErrors()) {
            return "board/form";
        }
        boardRepository.save(board);
        return "redirect:/board/list";
    }

    @PostMapping("/{boardId}/comments")
    public String addComment(@PathVariable Long boardId, String content,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setBoard(board);

            // 현재 사용자를 댓글 작성자로 설정
            User username = userRepository.findByUsername(userDetails.getUsername());
            comment.setUser(username);

            commentRepository.save(comment);
        }
        return "redirect:/board/" + boardId;
    }



    @GetMapping("/{boardId}/comments")
    public List<Comment> getComments(@PathVariable Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }



    @GetMapping("/detail")
    public String View(Model model,Long id ){
        model.addAttribute("board",boardRepository.findById(id).get());


        return"board/detail";
    }

    @PostMapping("/update/{id}")
    public String BoardUpdate(@PathVariable("id") Long id, Board board,
                              Model model){

        Board boardTemp = boardRepository.findById(id).get();
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardRepository.save(boardTemp);

        model.addAttribute("message","글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "board/message";
    }

    @GetMapping("/modify/{id}")
    public String BoardModify(@PathVariable("id") Long id,
                              Model model){
        model.addAttribute("board",boardRepository.getById(id));

        return "board/modify";
    }

    @GetMapping("/delete")
    public String BoardDelete(Long id, Model model){

        boardRepository.deleteById(id);

        model.addAttribute("message","글 삭제가 완료 되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "board/message";
    }

    @PostMapping("/{boardId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long boardId, @PathVariable Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null && comment.getBoard().getId().equals(boardId)) {
            commentRepository.delete(comment);
        }
        return "redirect:/board/"+boardId;
    }



}
