package springboot.study.epidemicData.util;

import org.springframework.stereotype.Component;
import springboot.study.epidemicData.bean.DataBean;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public class Util {

    public Object createBean(Map valueMap, Class clazz) {
        Object obj = null;
        Map tempMap = null;
        try {
            obj = clazz.newInstance();
            if(obj instanceof DataBean) { // 第一次判断是否是 DataBean 数据，如果是，则更换 map 进行赋值
                tempMap = valueMap;
                // 更换数据 map
                valueMap = (Map) valueMap.get("total");
            }
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                setValue(obj, field, valueMap.get(field.getName()));
            }
            if(obj instanceof DataBean) { // 这里判断，是因为 DataBean 里有两个值--name和"todayAddConfirm"
                                          // 不在上面用于赋值的那个 map 里，所以需要另外对这两个属性进行赋值
                String name = (String) tempMap.get("name"); // 获取地区名
                Double todayAddConfirm = (Double) ((Map) tempMap.get("today")).get("confirm"); // 获取今日新增
                // 设置属性值
                Field nameField = clazz.getDeclaredField("name");
                setValue(obj, nameField, name);
                // 设置属性值
                Field todayAddField = clazz.getDeclaredField("todayAddConfirm");
                setValue(obj, todayAddField, todayAddConfirm);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private void setValue(Object obj, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(obj, value);
    }

}
