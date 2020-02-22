package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyCityDataBean {

    private String date;
    private String city;
    private Double confirm;
    private Double dead;
    private Double heal;
    private Double suspect;
    private String confirm_add;
}
