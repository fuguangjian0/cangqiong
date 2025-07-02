package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @ApiOperation("新增员工")
    @PostMapping
    public Result addEmp(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工，员工数据：{}", employeeDTO);
        return employeeService.addEmp(employeeDTO);
    }

    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result page(EmployeePageQueryDTO empDTO){
        log.info("员工分页查询，参数：{}", empDTO);
        return employeeService.pageQuery(empDTO);
    }

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 账号禁用/启用
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("员工账号禁用/启用")
    @PostMapping("/status/{status}")
    public Result disableAccount(@RequestParam("id") Long id, @PathVariable Integer status){
        log.info("员工账号禁用/启用：{}", status);
        return employeeService.disableAccount(status, id);
    }

    /**
     * 员工信息查询回显
     *
     * @param id
     * @return
     */
    @ApiOperation("员工信息查询回显")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("员工信息查询回显：{}", id);
        return Result.success(employeeService.getById(id));
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO
     * @return
     */
    @ApiOperation("员工信息修改")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改：{}", employeeDTO);
        return employeeService.updateEmp(employeeDTO);
    }

    /**
     * 员工密码修改
     *
     * @param passwordEditDTO
     * @return
     */
    @ApiOperation("员工密码修改")
    @PutMapping("/editPassword")
    public Result editPwd(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("员工密码修改：{}", passwordEditDTO);
        return employeeService.editPwd(passwordEditDTO);
    }


}
