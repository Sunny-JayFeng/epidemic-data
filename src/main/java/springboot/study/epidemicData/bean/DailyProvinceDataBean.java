package springboot.study.epidemicData.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyProvinceDataBean {

    private String date;
    private String country;
    private String province;
    private Double confirm;
    private Double dead;
    private Double heal;
    private String confirm_add;
}
