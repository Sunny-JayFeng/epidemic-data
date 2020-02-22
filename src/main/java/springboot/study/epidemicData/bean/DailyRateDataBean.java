package springboot.study.epidemicData.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyRateDataBean {

    private String date;
    private String hubeiRate;
    private String notHubeiRate;
    private String countryRate;
}
