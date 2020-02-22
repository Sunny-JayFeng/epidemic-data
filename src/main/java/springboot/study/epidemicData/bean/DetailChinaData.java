package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailChinaData {

    private String area;
    private Double addConfirm;
    private Double confirm;
    private Double heal;
    private Double dead;
    private List<DataBean> cityList;
}
