package com.ces.intern.apitimecloud.service.impl;

import com.ces.intern.apitimecloud.dto.TaskDTO;
import com.ces.intern.apitimecloud.entity.ProjectEntity;
import com.ces.intern.apitimecloud.entity.TaskEntity;
import com.ces.intern.apitimecloud.http.exception.NotFoundException;
import com.ces.intern.apitimecloud.repository.ProjectRepository;
import com.ces.intern.apitimecloud.repository.TaskRepository;
import com.ces.intern.apitimecloud.service.TaskService;
import com.ces.intern.apitimecloud.util.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ModelMapper modelMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, ModelMapper modelMapper){
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TaskDTO createTask(Integer projectId, TaskDTO taskDTO, String userId) {
        ProjectEntity project = projectRepository.findById(projectId).
                orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()+" with "+ projectId));

        TaskEntity taskEntity = modelMapper.map(taskDTO,TaskEntity.class);
        //Integer userID = Integer.parseInt(userId);
        taskEntity.setName(taskDTO.getName());
        taskEntity.setCreateAt(new Date());
        taskEntity.setModifyAt(new Date());
        taskEntity.setProject(project);

        taskEntity = taskRepository.save(taskEntity);

        modelMapper.map(taskEntity,taskDTO);
        return  taskDTO;
    }

    @Override
    public TaskDTO getTask(Integer taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId).
                orElseThrow(()-> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD+" with "+taskId));
        TaskDTO taskDTO = modelMapper.map(taskEntity,TaskDTO.class);
        return taskDTO;
    }

    @Override
    public List getAllTaskByProject(Integer projectId) {
        List<TaskEntity> taskEntities = taskRepository.getAllByProjectId(projectId);
        if(taskEntities.size()==0) throw new NotFoundException
                (ExceptionMessage.NOT_FOUND_RECORD.getMessage()+ " with "+projectId);

        return taskEntities.stream().map(task -> modelMapper.map(task,TaskDTO.class)).collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateTask(Integer projectId, TaskDTO taskDTO) {
        TaskEntity taskEntity = taskRepository.findById(projectId).
                orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD+" with "+ projectId));

        taskEntity.setName(taskDTO.getName());
        taskEntity.setModifyAt(new Date());

        taskEntity = taskRepository.save(taskEntity);
        modelMapper.map(taskEntity,taskDTO);
        return taskDTO;
    }

    @Override
    public void deleteTask(Integer[] ids) {
        for(Integer item:ids){
            if(taskRepository.existsById(item)) {
                taskRepository.deleteById(item);
            } else {
                throw new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()+ " with "+item);
            }
        }
    }
}