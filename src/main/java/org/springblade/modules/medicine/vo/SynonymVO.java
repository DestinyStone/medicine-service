package org.springblade.modules.medicine.vo;

import lombok.Data;
import org.springblade.modules.medicine.entity.Synonym;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:15
 * @Description:
 */
@Data
public class SynonymVO extends Synonym {

    private List<GrossDictVO> dictList;
}
