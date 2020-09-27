package com.ces.intern.apitimecloud.controller;


import com.ces.intern.apitimecloud.dto.CompanyDTO;
import com.ces.intern.apitimecloud.dto.ProjectDTO;
import com.ces.intern.apitimecloud.dto.UserDTO;
import com.ces.intern.apitimecloud.dto.UserRoleDTO;
import com.ces.intern.apitimecloud.entity.ProjectEntity;
import com.ces.intern.apitimecloud.entity.UserRoleEntity;
import com.ces.intern.apitimecloud.http.exception.BadRequestException;
import com.ces.intern.apitimecloud.http.request.CompanyRequest;
import com.ces.intern.apitimecloud.http.request.ProjectRequest;
import com.ces.intern.apitimecloud.http.response.CompanyResponse;
import com.ces.intern.apitimecloud.http.response.ProjectResponse;
import com.ces.intern.apitimecloud.http.response.UserResponse;
import com.ces.intern.apitimecloud.http.response.UserRoleResponse;
import com.ces.intern.apitimecloud.repository.UserRoleRepository;
import com.ces.intern.apitimecloud.security.config.SecurityContact;
import com.ces.intern.apitimecloud.service.CompanyService;
import com.ces.intern.apitimecloud.service.ProjectService;
import com.ces.intern.apitimecloud.service.UserRoleService;
import com.ces.intern.apitimecloud.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
//@ApiImplicitParams({@ApiImplicitParam(name="authorization", value="JWT TOKEN", paramType="header")}) use for each method
public class CompanyController {
    private final CompanyService companyService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserRoleService userRoleService;


    @Autowired
    public CompanyController(CompanyService companyService,
                             ModelMapper modelMapper,
                             UserService userService,
                             ProjectService projectService,
                             UserRoleService userRoleService
                             ){
        this.companyService = companyService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.projectService = projectService;
        this.userRoleService = userRoleService;
    }

    @GetMapping(value = "/{id}")
    public CompanyResponse getCompany(@PathVariable Integer id, @RequestHeader(SecurityContact.HEADER_STRING) String userId ) throws Exception  {

        CompanyDTO company = companyService.getCompany(id);

        return modelMapper.map(company, CompanyResponse.class);

    }

    @PostMapping
    public CompanyResponse createCompany(@RequestBody CompanyRequest request,
                                         @RequestHeader("userId") Integer userId){

        if(request.getName() ==  null || userId == null ) throw new BadRequestException("Missing require field (Company name or UserID - header)");

        CompanyDTO company = modelMapper.map(request, CompanyDTO.class);

        return modelMapper.map(companyService.createCompany(company, userId), CompanyResponse.class);
    }

    @PutMapping(value = "/{id}")
    public CompanyResponse updateCompany(@PathVariable Integer id, @RequestBody CompanyRequest request, @RequestHeader("userId") Integer userId){

        CompanyDTO company = modelMapper.map(request, CompanyDTO.class);

        return modelMapper.map(companyService.updateCompany(id, company, userId), CompanyResponse.class);
    }

    @DeleteMapping(value = "/{id}")
    public String deleteCompany(@PathVariable Integer id){

        companyService.deleteCompany(id);
        return "OK";
    }

    @GetMapping(value = "/{id}/users")
    public List<UserRoleResponse> getUsersByCompanyId(@PathVariable Integer id){

        List<UserRoleDTO> users =  userService.getAllByCompanyId(id);

        return users.stream()
                .map(user -> modelMapper.map(user, UserRoleResponse.class))
                .collect(Collectors.toList());

    }

    @GetMapping(value = "/{companyId}/users/role/{roleId}")
    public List<UserResponse> getUsersByCompanyIdAndRoleId(@PathVariable(value = "companyId") Integer companyId, @PathVariable Integer roleId){

        List<UserDTO> users =  userService.getAllByCompanyAndRole(companyId, roleId);

        return users.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());

    }
    @GetMapping(value = "/{id}/projects")
    public List<ProjectResponse> getProjects(@PathVariable Integer id){

        List<ProjectDTO> projects = projectService.getAllByCompanyId(id);
        return projects.stream()
                .map(project  -> modelMapper.map(project, ProjectResponse.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/projects")
    public ProjectResponse createProject(@RequestBody ProjectRequest request, @PathVariable Integer id,
                                         @RequestHeader("userId") Integer userId){

        ProjectDTO project = modelMapper.map(request, ProjectDTO.class);

        ProjectDTO projectDTO = projectService.createProject(id,project,userId);
        return modelMapper.map(projectDTO, ProjectResponse.class);
    }


    @PostMapping("/{companyId}/users/{userId}")
    public UserResponse addUserToCompany(@PathVariable Integer companyId, @PathVariable Integer userId ){
        return modelMapper.map(userRoleService.addUserToCompany(userId, companyId), UserResponse.class);
    }
}
