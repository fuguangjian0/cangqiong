package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // 后期需要进行md5加密，然后再进行比对
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!pwd.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    @Override
    public Result addEmp(EmployeeDTO employeeDTO) {
        if (BeanUtil.isEmpty(employeeDTO)) {
            log.error("新增员工参数错误");
            return Result.error("新增员工参数错误");
        }
        Employee employee = new Employee();
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setCreateUser(Long.valueOf(CurrentHolder.getCurrentId()));
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        BeanUtil.copyProperties(employeeDTO, employee);
        boolean save = this.save(employee);
        if (save) {
            log.info("新增员工成功");
            return Result.success("新增员工成功");
        } else {
            log.error("新增员工失败");
            return Result.error("新增员工失败");
        }
    }


    /**
     * 员工分页查询
     *
     * @param empDTO
     * @return
     */
    @Override
    public Result<Page<Employee>> pageQuery(EmployeePageQueryDTO empDTO) {
        log.info("员工分页查询");
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(empDTO.getName())) {
            employeeLambdaQueryWrapper.like(Employee::getName, empDTO.getName());
        }
        Page<Employee> page = new Page<>(empDTO.getPage(), empDTO.getPageSize());
        Page<Employee> page1 = this.page(page, employeeLambdaQueryWrapper);
        log.info("员工分页查询结果：{}", page1);
        if (page1 == null || page1.getTotal() == 0 || page1.getRecords().isEmpty()) {
            log.error("没有查询到员工信息");
            return Result.error("没有查询到员工信息");
        }
        log.info("员工分页查询结果：{}", page1);
        return Result.success(page1);
    }

    /**
     * 员工账号禁用/启用
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result disableAccount(Integer status, Long id) {
        log.info("员工账号禁用/启用");
        Employee employee = this.getById(id);
        if (employee == null) {
            log.error("员工不存在");
            return Result.error("员工不存在");
        }
        employee.setStatus(status);
        boolean update = this.updateById(employee);
        if (!update) {
            log.error("员工账号禁用/启用失败");
            return Result.error("员工账号禁用/启用失败");
        }
        return Result.success("员工账号禁用/启用成功");
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO
     * @return
     */
    @AutoFill(value = OperationType.UPDATE)
    @Override
    public Result updateEmp(EmployeeDTO employeeDTO) {
        log.info("修改员工信息");
        Employee employee = this.getById(employeeDTO.getId());
        if (employee == null) {
            log.error("员工不存在");
            return Result.error("员工不存在");
        }
//        employee.setUpdateUser(Long.valueOf(CurrentHolder.getCurrentId()));
//        employee.setUpdateTime(LocalDateTime.now());
        if (employeeDTO.getId() == null) {
            log.error("员工信息修改失败");
            return Result.error("员工信息修改失败");
        }
        if (StringUtils.isNotBlank(employeeDTO.getUsername()))
            employee.setUsername(employeeDTO.getUsername());
        if (StringUtils.isNotBlank(employeeDTO.getName()))
            employee.setName(employeeDTO.getName());
        if (StringUtils.isNotBlank(employeeDTO.getPhone()))
            employee.setPhone(employeeDTO.getPhone());
        if (StringUtils.isNotBlank(employeeDTO.getSex()))
            employee.setSex(employeeDTO.getSex());
        if (StringUtils.isNotBlank(employeeDTO.getIdNumber()))
            employee.setIdNumber(employeeDTO.getIdNumber());
        employee.setUpdateTime(employeeDTO.getUpdateTime());
        employee.setUpdateUser(employeeDTO.getUpdateUser());
        boolean update = this.updateById(employee);
        if (!update) {
            log.error("员工信息修改失败");
            return Result.error("员工信息修改失败");
        }
        return Result.success("员工信息修改成功");
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     * @return
     */
    @Override
    public Result editPwd(PasswordEditDTO passwordEditDTO) {
        log.info("修改密码");
        passwordEditDTO.setEmpId(Long.valueOf(CurrentHolder.getCurrentId()));
        Employee employee = this.getById(passwordEditDTO.getEmpId());
        if (employee == null) {
            log.error("员工不存在");
            return Result.error("员工不存在");
        }
        String pwd = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        if (!pwd.equals(employee.getPassword())) {
            log.error("旧密码错误");
            return Result.error("旧密码错误");
        }
        employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
        boolean update = this.updateById(employee);
        if (!update) {
            log.error("密码修改失败");
            return Result.error("密码修改失败");
        }
        return Result.success("密码修改成功");
    }


}
