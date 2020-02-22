package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChinaTotalDataBean {

    private Double confirm;
    private Double suspect;
    private Double dead;
    private Double heal;
    private Double nowConfirm;
    private Double nowSevere;
}
