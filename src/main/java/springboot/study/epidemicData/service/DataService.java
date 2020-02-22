package springboot.study.epidemicData.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import springboot.study.epidemicData.bean.*;
import springboot.study.epidemicData.data.DataHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    @Autowired
    private DataHandler dataHandler;

    public void chinaTotalData(Model model) {
        model.addAttribute("chinaTotal", dataHandler.getTodayChinaTotal());
    }

    public void updateTime(Model model) {
        String lastUpdateTime = dataHandler.getLastUpdateTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(lastUpdateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date nowTime = new Date();
        Long time = (nowTime.getTime() - date.getTime()) / 1000 / 60;
        Long minutes = time % 60;
        Long hour = time / 60;
        StringBuilder resultTime = new StringBuilder();
        if(hour != 0) {
            resultTime.append(hour);
            resultTime.append(" 小时 ");
        }
        resultTime.append(minutes);
        resultTime.append(" 分钟");

        model.addAttribute("lastUpdateTime", lastUpdateTime);
        model.addAttribute("timeBefore", resultTime.toString());
    }

    public void todayChinaAdd(Model model) {
        model.addAttribute("chinaAdd", dataHandler.getTodayChinaAdd());
        Double todayAddSuspect = dataHandler.getChinaDayAddList().get(dataHandler.getChinaDayAddList().size() - 1).getSuspect();
        model.addAttribute("todayAddSuspect", todayAddSuspect);
    }

    public void chinaConfirmMap(Model model) {
        List<DataBean> chinaProvinceList = dataHandler.getChinaProvinceList();

        List<MapData> chinaConfirmList = new LinkedList<>();
        List<MapData> chinaNowConfirmList = new LinkedList<>();
        DataBean dataBean = null;
        for(int i = 0; i < chinaProvinceList.size(); i ++) {
            dataBean = chinaProvinceList.get(i);
            String name = dataBean.getName();
            Double confirm = dataBean.getConfirm();
            chinaConfirmList.add(new MapData(name, confirm));
            chinaNowConfirmList.add(new MapData(name, confirm - dataBean.getHeal() - dataBean.getDead()));
        }

        model.addAttribute("chinaConfirmList", chinaConfirmList);
        model.addAttribute("chinaNowConfirmList", chinaNowConfirmList);
    }

    public void countryChartOne(Model model) {
        List chinaDayAddList = dataHandler.getChinaDayAddList();
        List<String> dateList = new LinkedList<>();
        List<Double> addConfirmList = new LinkedList<>();
        List<Double> addSuspectList = new LinkedList<>();
        ChinaDayDataBean chinaDayDataBean = null;
        for(int i = 0; i < chinaDayAddList.size(); i ++) {
            chinaDayDataBean = (ChinaDayDataBean)chinaDayAddList.get(i);
            addConfirmList.add(chinaDayDataBean.getConfirm());
            addSuspectList.add(chinaDayDataBean.getSuspect());
            dateList.add(chinaDayDataBean.getDate());
        }

        model.addAttribute("dateListOne", dateList);
        model.addAttribute("addConfirmList", addConfirmList);
        model.addAttribute("addSuspectList", addSuspectList);

    }

    public void countryChartTwoThreeFour(Model model) {
        List chinaDayTotalList = dataHandler.getChinaDayTotalList();
        List<String> dateList = new LinkedList<>();

        // Two
        List<Double> confirmList = new LinkedList<>();
        List<Double> nowConfirmList = new LinkedList<>();
        List<Double> suspectList = new LinkedList<>();
        List<Double> nowSevereList = new LinkedList<>();

        // Three
        List<Double> healList = new LinkedList<>();
        List<Double> deadList = new LinkedList<>();

        // Four
        List<String> healRateList = new LinkedList<>();
        List<String> deadRateList = new LinkedList<>();

        ChinaDayDataBean chinaDayDataBean = null;
        for(int i = 0; i < chinaDayTotalList.size(); i ++) {
            chinaDayDataBean = (ChinaDayDataBean) chinaDayTotalList.get(i);
            dateList.add(chinaDayDataBean.getDate());

            // Two
            confirmList.add(chinaDayDataBean.getConfirm());
            nowConfirmList.add(chinaDayDataBean.getNowConfirm());
            suspectList.add(chinaDayDataBean.getSuspect());
            nowSevereList.add(chinaDayDataBean.getNowSevere());

            // Three
            healList.add(chinaDayDataBean.getHeal());
            deadList.add(chinaDayDataBean.getDead());

            // Four
            healRateList.add(chinaDayDataBean.getHealRate());
            deadRateList.add(chinaDayDataBean.getDeadRate());
        }

        model.addAttribute("dateListTwo", dateList);
        // Two
        model.addAttribute("confirmList", confirmList);
        model.addAttribute("nowConfirmList", nowConfirmList);
        model.addAttribute("suspectList", suspectList);
        model.addAttribute("nowSevereList", nowSevereList);

        // Three
        model.addAttribute("healList", healList);
        model.addAttribute("deadList", deadList);
        // Four
        model.addAttribute("healRateList", healRateList);
        model.addAttribute("deadRateList", deadRateList);
    }

    public void contrastChartOne(Model model) {
        List<ChinaDayDataBean> chinaDayTotalList = dataHandler.getChinaDayTotalList();
        List<DailyProvinceDataBean> dailyProvinceList = dataHandler.getProvinceData("湖北");
        List<DailyCityDataBean> dailyCityList = dataHandler.getCityData("湖北", "武汉");

        List<String> dateList = new LinkedList<>();
        List<Double> countryConfirmList = new LinkedList<>();
        List<Double> hubeiConfirmList = new LinkedList<>();
        List<Double> wuhanConfirmList = new LinkedList<>();

        for(int i = 0; i < dailyProvinceList.size(); i ++) {
            dateList.add(dailyProvinceList.get(i).getDate());
            hubeiConfirmList.add(dailyProvinceList.get(i).getConfirm());
        }
        for(int i = 0; i < dailyCityList.size(); i ++) {
            wuhanConfirmList.add(dailyCityList.get(i).getConfirm());
        }
        for(int i = chinaDayTotalList.size() - dailyProvinceList.size() + 1; i < chinaDayTotalList.size(); i ++) {
            countryConfirmList.add(chinaDayTotalList.get(i).getConfirm());
        }

        model.addAttribute("dateListThree", dateList);
        model.addAttribute("countryConfirmList", countryConfirmList);
        model.addAttribute("hubeiConfirmList", hubeiConfirmList);
        model.addAttribute("wuhanConfirmList", wuhanConfirmList);

    }

    public void contrastChartTwo(Model model) {
        List<DailyNewAddBean> dailyNewAddList = dataHandler.getDailyNewAddList();
        List<String> dateList = new LinkedList<>();
        List<Double> countryAddList = new LinkedList<>();
        List<Double> hubeiAddList = new LinkedList<>();
        List<Double> notHubeiAddList = new LinkedList<>();

        DailyNewAddBean dailyNewAddBean = null;
        for(int i = 0; i < dailyNewAddList.size(); i ++) {
            dailyNewAddBean = dailyNewAddList.get(i);
            dateList.add(dailyNewAddBean.getDate());
            countryAddList.add(dailyNewAddBean.getCountry());
            hubeiAddList.add(dailyNewAddBean.getHubei());
            notHubeiAddList.add(dailyNewAddBean.getNotHubei());
        }

        model.addAttribute("dateListFour", dateList);
        model.addAttribute("countryAddList", countryAddList);
        model.addAttribute("hubeiAddList", hubeiAddList);
        model.addAttribute("notHubeiAddList", notHubeiAddList);
    }

    public void contrastChartThree(Model model) {
        List<DailyRateDataBean> dailyDeadRateList = dataHandler.getDailyDeadRateList();

        List<String> dateList = new LinkedList<>();
        List<String> countryDeadRate = new LinkedList<>();
        List<String> hubeiDeadRate = new LinkedList<>();
        List<String> notHubeiDeadRate = new LinkedList<>();

        DailyRateDataBean dailyRateDataBean = null;
        for(int i = 0; i < dailyDeadRateList.size(); i ++) {
            dailyRateDataBean = dailyDeadRateList.get(i);
            dateList.add(dailyRateDataBean.getDate());
            countryDeadRate.add(dailyRateDataBean.getCountryRate());
            hubeiDeadRate.add(dailyRateDataBean.getHubeiRate());
            notHubeiDeadRate.add(dailyRateDataBean.getNotHubeiRate());
        }

        model.addAttribute("dateListFive", dateList);
        model.addAttribute("countryDeadRate", countryDeadRate);
        model.addAttribute("hubeiDeadRate", hubeiDeadRate);
        model.addAttribute("notHubeiDeadRate", notHubeiDeadRate);
    }

    public void contrastChartFour(Model model) {
        List<DailyRateDataBean> dailyHealRateList = dataHandler.getDailyHealRateList();

        List<String> dateList = new LinkedList<>();
        List<String> countryHealRate = new LinkedList<>();
        List<String> hubeiHealRate = new LinkedList<>();
        List<String> notHubeiHealRate = new LinkedList<>();

        DailyRateDataBean dailyRateDataBean = null;
        for(int i = 0; i < dailyHealRateList.size(); i ++) {
            dailyRateDataBean = dailyHealRateList.get(i);
            dateList.add(dailyRateDataBean.getDate());
            countryHealRate.add(dailyRateDataBean.getCountryRate());
            hubeiHealRate.add(dailyRateDataBean.getHubeiRate());
            notHubeiHealRate.add(dailyRateDataBean.getNotHubeiRate());
        }

        model.addAttribute("dateListSix", dateList);
        model.addAttribute("countryHealRate", countryHealRate);
        model.addAttribute("hubeiHealRate", hubeiHealRate);
        model.addAttribute("notHubeiHealRate", notHubeiHealRate);
    }

    public void chinaProvinceCityData(Model model) {
        List<DataBean> chinaProvinceList = dataHandler.getChinaProvinceList();
        String[] provinceName = new String[]{};
        Map provinceCityMap = dataHandler.getProvinceCityMap();

        List<DetailChinaData> detailChinaDataList = new LinkedList<>();
        DetailChinaData detailChinaData = null;
        DataBean province = null;
        for(int i = 0; i < chinaProvinceList.size(); i ++) {
            province = chinaProvinceList.get(i);
            String area = province.getName();
            Double addConfirm = province.getTodayAddConfirm();
            Double confirm = province.getConfirm();
            Double heal = province.getHeal();
            Double dead = province.getDead();
            detailChinaDataList.add(new DetailChinaData(area, addConfirm, confirm, heal, dead, (List<DataBean>)provinceCityMap.get(area)));
        }
        model.addAttribute("detailChinaDataList", detailChinaDataList);
    }

    public void foreignData(Model model) {
        List<DataBean> countryList = dataHandler.getCountryList();
        Double foreignConfirmSum = 0d;
        Double foreignDeadSum = 0d;
        DataBean dataBean = null;
        boolean isDeleteChina = false;
        for(int i = 0; i < countryList.size(); i ++) {
            dataBean = countryList.get(i);
            if(!isDeleteChina && "中国".equals(dataBean.getName())) { // 海外数据不包含中国
                countryList.remove(i);
                isDeleteChina = true;
                continue;
            }
            foreignConfirmSum += dataBean.getConfirm();
            foreignDeadSum += dataBean.getDead();
        }
        model.addAttribute("foreignConfirmSum", foreignConfirmSum);
        model.addAttribute("foreignDeadSum", foreignDeadSum);
        model.addAttribute("countryList", countryList);
    }
}
