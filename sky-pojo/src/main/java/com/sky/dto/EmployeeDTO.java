package com.sky.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("员工登录信息")
public class EmployeeDTO implements Serializable {

    @ApiModelProperty("员工id")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("密码")
    private String phone;

    @ApiModelProperty("员工性别")
    private String sex;

    @ApiModelProperty("身份证号")
    private String idNumber;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    private Long createUser;

    @ApiModelProperty("更新人")
    private Long updateUser;
}
