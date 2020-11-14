package com.ces.intern.apitimecloud.controller;

import com.ces.intern.apitimecloud.dto.CommentDTO;
import com.ces.intern.apitimecloud.dto.DiscussionDTO;
import com.ces.intern.apitimecloud.http.exception.BadRequestException;
import com.ces.intern.apitimecloud.http.request.DiscussionRequest;
import com.ces.intern.apitimecloud.service.CommentService;
import com.ces.intern.apitimecloud.service.DiscussionService;
import com.ces.intern.apitimecloud.service.impl.CommentServiceImpl;
import com.ces.intern.apitimecloud.service.impl.DiscussionServiceImpl;
import com.ces.intern.apitimecloud.util.ExceptionMessage;
import com.ces.intern.apitimecloud.util.ResponseMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final CommentService commentService;

    DiscussionController(DiscussionServiceImpl discussionService,
                         CommentServiceImpl commentService){
        this.discussionService = discussionService;
        this.commentService = commentService;
    }

    @PostMapping("")
    private DiscussionDTO create(@RequestBody DiscussionRequest discussionRequest){
        if(discussionRequest.getContent().isEmpty()) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.name());
        return discussionService.create(discussionRequest);
    }

    @PutMapping("/{discussionId}")
    private DiscussionDTO update(@RequestBody DiscussionRequest discussionRequest,
                                 @PathVariable("discussionId") Integer discussionId,
                                 @RequestHeader("userId") Integer userId){
        return discussionService.update(discussionRequest, discussionId, userId);
    }

    @DeleteMapping(value = "/{discussionId}")
    private String delete(@PathVariable("discussionId")Integer discussionId){
        discussionService.delete(discussionId);
        return ResponseMessage.DELETE_SUCCESS;
    }

    @GetMapping("/{discussionId}/comments")
    private List<CommentDTO> getAllCommentByDiscussionId(@PathVariable("discussionId")Integer discussionId){
        if(discussionId == null) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage() + "discussionId");
        return commentService.getAllCommentByDiscussionId(discussionId);
    }

}