package org.springblade.modules.medicine.dto;

import com.baomidou.mybatisplus.extension.service.IService;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 01:39
 * @Description:
 */
@Data
@Builder
public class RebackDTO {
    private IService service;

    private String fileName;
}
