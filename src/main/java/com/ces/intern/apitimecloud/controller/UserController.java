package com.ces.intern.apitimecloud.controller;

import com.ces.intern.apitimecloud.dto.*;
import com.ces.intern.apitimecloud.http.exception.BadRequestException;
import com.ces.intern.apitimecloud.http.request.UserRequest;
import com.ces.intern.apitimecloud.http.response.*;
import com.ces.intern.apitimecloud.repository.ProjectUserRepository;
import com.ces.intern.apitimecloud.repository.TimeRepository;
import com.ces.intern.apitimecloud.service.ProjectService;
import com.ces.intern.apitimecloud.service.TaskService;
import com.ces.intern.apitimecloud.service.TimeService;
import com.ces.intern.apitimecloud.service.UserService;
import com.ces.intern.apitimecloud.util.ExceptionMessage;
import com.ces.intern.apitimecloud.util.ResponseMessage;
import com.ces.intern.apitimecloud.util.Utils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
//@ApiImplicitParams({@ApiImplicitParam(name="authorization", value="JWT TOKEN", paramType="header")}) use for each method
public class UserController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final TimeService timeService;
    private final TaskService taskService;

    @Autowired
    public UserController(UserService userService,
                          ProjectService projectService,
                          ModelMapper modelMapper,
                          TimeService timeService,
                          TaskService taskService){
        this.userService = userService;
        this.projectService = projectService;
        this.modelMapper = modelMapper;
        this.timeService = timeService;
        this.taskService = taskService;
    }

    @PostMapping(value ="")
    public String createUser(@RequestBody UserRequest userRequest)
    {
        return userService.save(userRequest);
    }

    @GetMapping("/{id}")

    public UserResponse findUser(@PathVariable Integer id)
    {
        return userService.findUser(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@RequestBody UserRequest userRequest, @PathVariable Integer id, @RequestHeader Integer userId)
    {
        return userService.update(userRequest, id, userId);
    }

    @DeleteMapping(value = "/{userId}")

    public String deleteUser(@PathVariable Integer userId)
    {
        userService.delete(userId);
        return ResponseMessage.DELETE_SUCCESS;
    }

    @GetMapping("/{id}/projects")
    public List<ProjectResponse> getProjectsByUserId(@PathVariable("id") Integer userId){
        List<ProjectDTO> projects = projectService.getAllByUserId(userId);
        projects.sort((o1, o2) -> (int)(o1.getCreateAt().getTime() - o2.getCreateAt().getTime()));
        return projects.stream()
                .map(project  -> modelMapper.map(project, ProjectResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/projects/task-count")
    public List<ProjectResponse> getProjectsByUserIdOrderByTaskCount(@PathVariable("id") Integer userId){
        List<ProjectDTO> projects = projectService.getAllByUserIdOOrderByTaskCount(userId);
        return projects.stream()
                .map(project  -> modelMapper.map(project, ProjectResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/projects-available")
    public List<ProjectUserResponse> getAllProjectsByUserIdAndIsDoing(@PathVariable("id") Integer userId){
        List<ProjectUserDTO> projectUsers = projectService.getAllByUserIdAndIsDoing(userId,true);
        return projectUsers.stream().map(projectUser->modelMapper.map(projectUser,ProjectUserResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/times")
    public List<TimeResponse> getTimesByUserId(@PathVariable("id") Integer userId){
        List<TimeDTO> times = timeService.getTimesByUserId(userId);

        return times.stream()
                .map(time  -> modelMapper.map(time, TimeResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/total-times")
    public Float getSumTimeByUserId(@PathVariable("userId") Integer userId){
        if(userId == null) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage() + "userID");
        return timeService.sumTimeByUserId(userId);
    }

    @GetMapping("{id}/tasks")
    public List<TaskResponse> getAllTasksByUserId(@PathVariable("id") Integer userId){
        List<TaskDTO> tasks = taskService.getAllTaskByUser(userId);
        return tasks.stream().map(task->modelMapper.map(task,TaskResponse.class)).collect(Collectors.toList());
    }

    @GetMapping("/{userId}/description/{description}/total-times")
    public Float getSumTimeByUserDescription(@PathVariable("userId") Integer userId,@PathVariable("description") String description){
        if(userId == null || description == null ) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage() + "userID" +" or "+"description");
        return timeService.sumTimeByUserDescription(userId,description);
    }

    @GetMapping("/{userId}/date/{date}/total-times")
    public Float getSumTimeByDayOfUser(@PathVariable("userId") Integer userId,@PathVariable("date") String date) throws ParseException {
        if(userId == null || date == null ) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage() + "userID" +" or "+"date");
        return timeService.sumTimeByDayOfUser(userId,date,Utils.toNumbersOfDay(date,1));
    }

    @GetMapping("/{userId}/week/{date}/total-times")
    public Float getSumTimeByWeekOfUser(@PathVariable("userId") Integer userId,@PathVariable("date") String date) throws ParseException {
        if(userId == null || date == null ) throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage() + "userID" +" or "+"date");
        return timeService.sumTimeByWeekOfUser(userId,date);
    }
}
