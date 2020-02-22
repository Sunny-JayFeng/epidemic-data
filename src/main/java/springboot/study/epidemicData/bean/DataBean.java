package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBean {

    private String name;
    private Double confirm;
    private Double todayAddConfirm;
    private Double suspect;
    private Double heal;
    private String healRate;
    private Double dead;
    private String deadRate;
}
