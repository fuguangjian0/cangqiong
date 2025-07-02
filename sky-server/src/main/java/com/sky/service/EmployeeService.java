package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.Result;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    Result addEmp(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param empDTO
     * @return
     */
    Result pageQuery(EmployeePageQueryDTO empDTO);

    /**
     * 账号禁用或者启用
     * @param status
     * @param id
     * @return
     */
    Result disableAccount(Integer status, Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    Result updateEmp(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param passwordEditDTO
     * @return
     */
    Result editPwd(PasswordEditDTO passwordEditDTO);

}
