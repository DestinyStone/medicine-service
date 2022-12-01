package org.springblade.modules.medicine.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/1/28 11:00
 * @Description: 用于生成唯一码
 */
@Data
@TableName("bus_code")
@ApiModel(value = "用于生成唯一码", description = "用于生成唯一码")
public class BusCode {

	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty("前缀")
	private String prefix;

	@ApiModelProperty("编码")
	private Long code;

	@ApiModelProperty("分隔符")
	private String separate;

	@ApiModelProperty("标识")
	private String flag;

	@ApiModelProperty("更新日期")
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
