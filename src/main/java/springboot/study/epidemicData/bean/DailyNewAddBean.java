package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyNewAddBean {
    private String date;
    private Double hubei;
    private Double country;
    private Double notHubei;
}
