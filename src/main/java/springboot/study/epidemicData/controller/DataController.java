package springboot.study.epidemicData.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot.study.epidemicData.data.DataHandler;
import springboot.study.epidemicData.service.DataService;

import java.util.List;
import java.util.Map;

@Controller
public class DataController {

    @Autowired
    private DataHandler dataHandler;

    @Autowired
    private DataService dataService;

    @GetMapping("/index")
    public String index(Model model) {
        if(!dataHandler.getIsFlushed()) dataHandler.initAllData();
        dataService.updateTime(model);
        dataService.chinaTotalData(model);
        dataService.todayChinaAdd(model);
        dataService.countryChartOne(model);
        dataService.countryChartTwoThreeFour(model);
        dataService.contrastChartOne(model);
        dataService.contrastChartTwo(model);
        dataService.contrastChartThree(model);
        dataService.contrastChartFour(model);
        dataService.chinaConfirmMap(model);
        dataService.chinaProvinceCityData(model);
        dataService.foreignData(model);
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public Map getData() {
        return dataHandler.getData();
    }

    @ResponseBody
    @RequestMapping(value = "/provinceData", method = RequestMethod.GET)
    public List getProvinceData(@RequestParam("province") String province) {
        return dataHandler.getProvinceData(province);
    }

    @ResponseBody
    @RequestMapping(value = "/cityData", method = RequestMethod.GET)
    public List getCityData(@RequestParam("province") String province, @RequestParam("city") String city) {
        return dataHandler.getCityData(province, city);
    }

}
