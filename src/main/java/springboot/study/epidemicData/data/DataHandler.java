package springboot.study.epidemicData.data;

import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import springboot.study.epidemicData.bean.*;
import springboot.study.epidemicData.myexception.RequestFailException;
import springboot.study.epidemicData.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class DataHandler {

//    @Autowired
    private Util util = new Util();

    private List<DataBean> countryList = new ArrayList<>();
    private List<DataBean> chinaProvinceList = new ArrayList<>();
    private Map<String, List<DataBean>> provinceCityMap = new HashMap<>();

    private List<ChinaDayDataBean> chinaDayTotalList = new ArrayList<>();  // 这个集合的数据，用于绘制 全国累计确诊/现有确诊/疑似/重症趋势图，
                                                                          // 全国累计治愈/死亡趋势图
                                                                          // 全国治愈率/死亡率趋势图
    private List<ChinaDayDataBean> chinaDayAddList = new ArrayList<>();  // 这个集合的数据，用于绘制 全国疫情新增趋势图

    private List<DailyNewAddBean> dailyNewAddList = new ArrayList<>(); // 这个集合的数据，用于绘制 湖北内外新增确诊对比图
    private List<DailyRateDataBean> dailyDeadRateList = new ArrayList<>(); // 这个集合的数据，用于绘制 湖北内外病死率对比图
    private List<DailyRateDataBean> dailyHealRateList = new ArrayList<>(); // 这个集合的数据，用于绘制 湖北内外治愈率对比图

    private Map<String, List<DailyProvinceDataBean>> dailyProvinceDataMap = new HashMap<>();
    private Map<String, List<DailyCityDataBean>> dailyCityDataMap = new HashMap<>();

    private Map allDataMap = null;
    private Boolean isFlushed = false;

    private String lastUpdateTime; // 上次更新时间
    private ChinaTotalDataBean todayChinaTotal; // 中国今日疫情数据
    private ChinaTotalDataBean todayChinaAdd; // 中国今日新增数据

    public static void main(String[] args) {
        DataHandler dataHandler = new DataHandler();
        dataHandler.initAllData();
        dataHandler.getCityData("湖北", "武汉");
        dataHandler.getProvinceData("湖北");
        System.out.println();
    }

    // 定时更新数据
    @Scheduled(cron = "0 0 0/3 * * ? *")
    private void flushData() {
        initAllData();
    }

    // 初始化单个数据
    private void initUpdateTimeAndTodayChina(Map dataMap) {
        // 上次更新时间
        lastUpdateTime = (String) dataMap.get("lastUpdateTime");
        // 初始化中国今日疫情数据
        todayChinaTotal = (ChinaTotalDataBean) util.createBean((Map)dataMap.get("chinaTotal"), ChinaTotalDataBean.class);
        // 初始化中国今日新增数据
        todayChinaAdd = (ChinaTotalDataBean) util.createBean((Map)dataMap.get("chinaAdd"), ChinaTotalDataBean.class);
    }

    // 将所有数据存到一个集合里
    private void initAllDataMap() {
        allDataMap = new HashMap();
        allDataMap.put("lastUpdateTime", lastUpdateTime);
        allDataMap.put("todayChinaTotal", todayChinaTotal);
        allDataMap.put("todayChinaAdd", todayChinaAdd);

        allDataMap.put("countryList", countryList);
        allDataMap.put("chinaProvinceList", chinaProvinceList);
        allDataMap.put("provinceCityMap", provinceCityMap);
        allDataMap.put("chinaDayTotalList", chinaDayTotalList);
        allDataMap.put("chinaDayAddList", chinaDayAddList);
        allDataMap.put("dailyNewAddList", dailyNewAddList);
        allDataMap.put("dailyDeadRateList", dailyDeadRateList);
        allDataMap.put("dailyHealRateList", dailyHealRateList);
    }

    // 初始化所有数据(areaTree的数据)
    public void initAllData() {
        Gson gson = new Gson();
        Map dataMap = gson.fromJson(getData("https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5", "data").toString(), Map.class);
        handleData(dataMap); // 处理数据
        initAllDataMap();
        isFlushed = true;
    }

    // 初始化保存中国今日疫情数据的集合
    private void initChinaDayTotalList(Map dataMap) {
        List chinaDayList = (List) dataMap.get("chinaDayList");
        addValueToCollection(chinaDayList, chinaDayTotalList, ChinaDayDataBean.class);
    }

    // 初始化保存中国今日疫情新增数据的集合
    private void initChinaDayAddList(Map dataMap) {
        List chinaAddList = (List) dataMap.get("chinaDayAddList");
        addValueToCollection(chinaAddList, chinaDayAddList, ChinaDayDataBean.class);
    }

    // 初始化保存世界各国疫情数据的集合
    private List initCountryList(Map dataMap) {
        List areaTreeList = (List) dataMap.get("areaTree");
        addValueToCollection(areaTreeList, countryList, DataBean.class); // 国家数据
        return areaTreeList;
    }

    // 初始化保存最近一段时间湖北内外新增情况数据集合
    private void initDailyNewAddList(Map dataMap) {
        List dailyNewAddHistory = (List) dataMap.get("dailyNewAddHistory");
        addValueToCollection(dailyNewAddHistory, dailyNewAddList, DailyNewAddBean.class);
    }

    // 初始化保存最近一段时间湖北内外病死率情况数据集合
    private void initDailyDeadRateList(Map dataMap) {
        List dailyDeadRateHistory = (List) dataMap.get("dailyDeadRateHistory");
        addValueToCollection(dailyDeadRateHistory, dailyDeadRateList, DailyRateDataBean.class);
    }

    // 初始化保存最近一段时间湖北内外治愈率情况数据集合
    private void initDailyHealRateList(Map dataMap) {
        List dailyHealRateHistory = (List) dataMap.get("dailyHealRateHistory");
        addValueToCollection(dailyHealRateHistory, dailyHealRateList, DailyRateDataBean.class);
    }

    // 初始化保存中国各省各市疫情数据的集合
    private void initProvinceDataAndCityData(Map dataMap) {
        List provinceList = (List)((Map)((List) dataMap.get("areaTree")).get(0)).get("children");
        for(int i = 0; i < provinceList.size(); i ++) {
            Map theProvince = (Map) provinceList.get(i);
            chinaProvinceList.add((DataBean) util.createBean(theProvince, DataBean.class));

            List cityList = (List) theProvince.get("children");
            List allCities = new ArrayList();
            if(cityList == null) continue;
            addValueToCollection(cityList, allCities, DataBean.class);
            provinceCityMap.put(theProvince.get("name").toString(), allCities); // 中国各省的各个市的数据
        }
    }

    // 初始化保存某个省最近一段时间的疫情数据的集合
    private void initDailyProvinceDataMap (List dataList, String area, Class clazz) {
        List<DailyProvinceDataBean> provinceDataList = new ArrayList<>();
        addValueToCollection(dataList, provinceDataList, clazz);
        dailyProvinceDataMap.put(area, provinceDataList);
    }

    // 初始化保存某个市最近一段时间的疫情数据的集合
    private void initDailyCityDataMap (List dataList, String area, Class clazz) {
        List<DailyCityDataBean> cityDataList = new ArrayList<>();
        addValueToCollection(dataList, cityDataList, clazz);
        dailyCityDataMap.put(area, cityDataList);
    }

    // 将数据添加到集合里
    private void addValueToCollection(List dataList, List targetList, Class clazz) {
        for(int i = 0; i < dataList.size(); i ++) {
            Map dataMap = (Map) dataList.get(i);
            targetList.add(util.createBean(dataMap, clazz));
        }
    }

    // 处理数据
    private void handleData(Map dataMap) {
        if(dataMap == null) return;
        initUpdateTimeAndTodayChina(dataMap); // 初始化更新时间和中国今日疫情情况
        initChinaDayTotalList(dataMap);  // 初始化保存中国今日疫情数据的集合
        initChinaDayAddList(dataMap);  // 初始化保存中国今日疫情新增数据的集合
        initCountryList(dataMap);  // 初始化保存世界各国疫情数据的集合
        initProvinceDataAndCityData(dataMap);  // 初始化保存中国各省各市疫情数据的集合
        initDailyNewAddList(dataMap);  // 初始化保存最近一段时间湖北内外新增情况数据集合
        initDailyDeadRateList(dataMap);  // 初始化保存最近一段时间湖北内外治愈率情况数据集合
        initDailyHealRateList(dataMap);  // 初始化保存最近一段时间湖北内外治愈率情况数据集合
    }

    private Object getData(String url, String data) {
        String resultStr = getDataByHttpURLConnection(url);
        if(resultStr == null) return null;
        Gson gson = new Gson();
        Map resultMap = gson.fromJson(resultStr, Map.class);
        return resultMap.get(data);
    }

    public Map getData() {
        if(allDataMap != null) return allDataMap;
        initAllData();
        return allDataMap;
    }

    // 获取某个省最近一段时间的疫情情况
    public List<DailyProvinceDataBean> getProvinceData(String province) {
        if(dailyProvinceDataMap.get(province) != null) return dailyProvinceDataMap.get(province);
        List dataList = (List) getData("https://api.inews.qq.com/newsqa/v1/query/pubished/daily/list?province=" + province, "data");
        initDailyProvinceDataMap(dataList, province, DailyProvinceDataBean.class);
        return dailyProvinceDataMap.get(province);
    }

    // 获取某个市最近一段时间的疫情情况
    public List<DailyCityDataBean> getCityData(String province, String city) {
        if(dailyCityDataMap.get(province + "-" + city) != null) return dailyCityDataMap.get(province + "-" + city);
        List dataList = (List) getData("https://api.inews.qq.com/newsqa/v1/query/pubished/daily/list?province=" + province + "&city=" + city, "data");
        initDailyCityDataMap(dataList, province + "-" + city, DailyCityDataBean.class);
        return dailyCityDataMap.get(province + "-" + city);
    }

    // 设置请求参数,发送请求
    private void setConnectionParam(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(60000);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.connect();
    }

    // 处理请求结果
    private String handleRequestResult(HttpURLConnection urlConnection) throws RequestFailException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            if(200 == urlConnection.getResponseCode()) {
                inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            }else {
                throw new RequestFailException("Request fail exception");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    private String getDataByHttpURLConnection(String urlString) {

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            setConnectionParam(urlConnection);
            return handleRequestResult(urlConnection);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

}
