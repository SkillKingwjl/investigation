package com.zztaiwll.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.zztaiwll.dao.AddInfo;
import com.zztaiwll.dao.VillageResearch;
import com.zztaiwll.service.ShowService;

public class ShowController extends Controller {
    private ShowService showService = enhance(ShowService.class);

    /*
     * 获取数据分析页显示的条目按钮
     */
    @Before(GET.class)
    public void getShowButton() throws Exception {
        int parent_id = getParaToInt("parent_id");
        int type = getParaToInt("type");
        List<AddInfo> list = showService.getShowFromAddInfo(parent_id);
        if (type == 1) {
            List<VillageResearch> dataHeadList = showService.getYearByParent_id(parent_id);
            List<Map<String, Object>> dataName = new ArrayList<Map<String, Object>>();
            for (VillageResearch DO : dataHeadList) {
                Map<String, Object> map = new HashMap<String, Object>();
                int year = DO.getInt("year");
                map.put("id", year);
                map.put("name", year);
                dataName.add(map);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("year", dataName);
            result.put("data", list);
            renderJson("result", result);
        }
        if (type == 2) {
            List<VillageResearch> dataHeadList = showService.getAddressByParent_id(parent_id);
            List<Map<String, Object>> dataName = new ArrayList<Map<String, Object>>();
            for (VillageResearch DO : dataHeadList) {
                Map<String, Object> map = new HashMap<String, Object>();
                int address_id = DO.getInt("address_id");
                map.put("id", address_id);
                if (address_id == 2901) {
                    map.put("id", address_id);
                    map.put("name", "陕北");
                } else if (address_id == 2902) {
                    map.put("id", address_id);
                    map.put("name", "关中");
                } else {
                    map.put("id", address_id);
                    map.put("name", "陕南");
                }
                dataName.add(map);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("address", dataName);
            result.put("data", list);
            renderJson("result", result);
        }
        if (type == 0) {
            renderJson("result", list);
        }

    }

    /*
     * 获取需要现实的数据
     */
    @Before(GET.class)
    public void getChartData() throws Exception {
        int info_id = getParaToInt("info_id");
        int type = getParaToInt("type");
        if (type == 0) {
            Map<String, Object> data = getDataAll(info_id);
            renderJson("result", data);
        } else if (type == 1) {
            int year = getParaToInt("year");
            Map<String, Object> data = getDataForAddress(info_id, year);
            ;
            renderJson("result", data);
        } else {
            int address_id = getParaToInt("address_id");
            Map<String, Object> data = getDataForYear(info_id, address_id);
            renderJson("result", data);
        }
    }

    /*
     * 获取所有的
     */
    private Map<String, Object> getDataAll(int info_id) {
        List<VillageResearch> list = showService.getShowVillageDataByInfoId(info_id);
        AddInfo addInfo = showService.getSingleAddInfoById(info_id);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> titleMap = new HashMap<String, Object>();
        titleMap.put("text", addInfo.get("name"));
        map.put("title", titleMap);
        String attr = addInfo.getStr("attr");
        map.put("attr", attr);
        List<Integer> dataName = new ArrayList<Integer>();
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (VillageResearch info : list) {
            String result = info.getStr("result");
            if (!isDecimal(result)) {
                continue;
            }
            int resultParams = (int) Double.parseDouble(result);
            if (info_id == 118 || info_id == 119 || info_id == 120 || info_id == 106 || info_id == 117 || info_id == 121 || info_id == 122 || info_id == 202) {
                if (resultParams < 0) {
                    continue;
                }
            } else {
                if (resultParams < 1) {
                    continue;
                }
            }

            if (resultParams > 10) {
                continue;
            }
            if (!result.endsWith(".0")) {
                continue;
            }
            dataName.add((int) Double.parseDouble(result));
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("data", info.getLong("num"));
            DecimalFormat df = new DecimalFormat("######0.00");
            double bai = info.getBigDecimal("bai").setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            String str = df.format(bai);
            dataMap.put("data1", str);
            data.add(dataMap);
        }
        if (data == null || data.size() == 0) {
            return null;
        }
        List<Map<String, Object>> xAxis = new ArrayList<Map<String, Object>>();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("type", "category");
        dataMap.put("boundaryGap", true);
        dataMap.put("data", dataName);
        xAxis.add(dataMap);
        map.put("xAxis", xAxis);
        List<Map<String, Object>> seriesList = new ArrayList<Map<String, Object>>();
        Map<String, Object> seriesData = new HashMap<String, Object>();
        seriesData.put("name", attr);
        seriesData.put("type", "bar");
        seriesData.put("data", data);
        seriesData.put("itemStyle", handleDataItemsStyle(1));
        if (data != null && data.size() < 6) {
            seriesData.put("barWidth", 60);
        }
        seriesList.add(seriesData);
        map.put("series", seriesList);
        return map;
    }


    private Map<String, Object> handleDataItemsStyle(int index) {
        String color[] = new String[]{"#ffc032", "#f47e39", "#66ac52", "#d9534f", "#00ffff", "#ffff00", "#1b6d85"};
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> normal = new HashMap<String, Object>();
        normal.put("color", color[index - 1]);
        result.put("normal", normal);
        return result;
    }

    /*
     * 判断是否是数字
     */
    public static boolean isDecimal(String str) {
        if (str == null || "".equals(str))
            return false;
        java.util.regex.Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    /*
     * 按照年度获取信息
     */
    public Map<String, Object> getDataForYear(int info_id, int address_id) {
        AddInfo addInfo = showService.getSingleAddInfoById(info_id);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> titleMap = new HashMap<String, Object>();
        titleMap.put("text", addInfo.get("name"));
        map.put("title", titleMap);
        String attr = addInfo.getStr("attr");
        map.put("attr", attr);
        List<String> dataName = new ArrayList<String>();
        List<VillageResearch> list = showService.getYearByInfo_id(info_id);
        for (VillageResearch vill : list) {
            int year = vill.getInt("year");
            dataName.add(year + "");
        }
        List<Map<String, Object>> xAxis = new ArrayList<Map<String, Object>>();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("type", "category");
        dataMap.put("boundaryGap", true);
        dataMap.put("data", dataName);
        Map<String, Object> dataMap1 = new HashMap<String, Object>();
        dataMap.put("type", "category");
        dataMap.put("boundaryGap", true);
        dataMap.put("data", dataName);
        xAxis.add(dataMap);
        xAxis.add(dataMap1);
        map.put("xAxis", xAxis);
        List<Map<String, Object>> seriesList = handleYearData(info_id, address_id);
        map.put("series", seriesList);
        return map;
    }

    /*
     * 按照地区获取信息
     */
    public Map<String, Object> getDataForAddress(int info_id, int year) {
        AddInfo addInfo = showService.getSingleAddInfoById(info_id);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> titleMap = new HashMap<String, Object>();
        titleMap.put("text", addInfo.get("name"));
        map.put("title", titleMap);
        String attr = addInfo.getStr("attr");
        map.put("attr", attr);
        List<VillageResearch> dataHeadList = showService.getAddressByInfo_id(info_id);
        List<String> dataName = new ArrayList<String>();
        for (VillageResearch DO : dataHeadList) {
            int address_id = DO.getInt("address_id");
            if (address_id == 2901) {
                dataName.add("陕北");
            } else if (address_id == 2902) {
                dataName.add("关中");
            } else {
                dataName.add("陕南");
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("data", dataName);
        map.put("xAxis", dataMap);
        List<Map<String, Object>> seriesList = handleAddressData(info_id, year);
        map.put("series", seriesList);
        return map;
    }

    /*
     * 获取年度数据
     */
    private List<Map<String, Object>> handleYearData(int info_id, int address_id) {
        List<String> resultList = this.getAnalyResult(info_id);
        List<Map<String, Object>> seriesList = new ArrayList<Map<String, Object>>();
        int i = 1;
        for (String resultParams : resultList) {
            if (!resultParams.endsWith(".0")) {
                continue;
            }
            int result = (int) Double.parseDouble(resultParams);
            if (info_id == 118 || info_id == 119 || info_id == 120 || info_id == 106 || info_id == 117 || info_id == 121 || info_id == 122 || info_id == 202) {
                if (result < 0) {
                    continue;
                }
            } else {
                if (result < 1) {
                    continue;
                }
            }
            if (result > 10) {
                continue;
            }
            Map<String, Object> itemsMap = new HashMap<String, Object>();
            List<VillageResearch> dataItemsList = showService.getShowVillageDataByInfoIdAndAddress(info_id, address_id, resultParams);
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            for (VillageResearch vill : dataItemsList) {
                if (StringUtils.isBlank(vill.get("result").toString())) {
                    continue;
                }
                Map<String, Object> dataMap = new HashMap<String, Object>();
                long num = vill.getLong("num");
                dataMap.put("data", num);
                DecimalFormat df = new DecimalFormat("######0.00");
                double bai;
                if (address_id == 0) {
                    bai = vill.getBigDecimal("bai").setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                } else {
                    bai = vill.getDouble("bai") * 100;
                }
                String str = df.format(bai);
                dataMap.put("data1", str);
                data.add(dataMap);
            }
            itemsMap.put("type", "bar");
            itemsMap.put("name", (int) Double.parseDouble(resultParams));
            itemsMap.put("data", data);
            itemsMap.put("itemStyle", handleDataItemsStyle(i));
            if (data != null && data.size() < 6) {
                itemsMap.put("barWidth", 60);
            }
            seriesList.add(itemsMap);
        }
        return seriesList;
    }

    /*
     * 处理地区数据
     */
    private List<Map<String, Object>> handleAddressData(int info_id, int year) {
        List<String> resultList = this.getAnalyResult(info_id);
        List<Map<String, Object>> seriesList = new ArrayList<Map<String, Object>>();
        int i = 1;
        for (String resultParams : resultList) {
            if (!resultParams.endsWith(".0")) {
                continue;
            }
            int result = (int) Double.parseDouble(resultParams);
            if (info_id == 118 || info_id == 119 || info_id == 120 || info_id == 106 || info_id == 117 || info_id == 121 || info_id == 122 || info_id == 202) {
                if (result < 0) {
                    continue;
                }
            } else {
                if (result < 1) {
                    continue;
                }
            }
            if (result > 10) {
                continue;
            }
            Map<String, Object> itemsMap = new HashMap<String, Object>();
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            List<VillageResearch> dataItemsList = showService.getShowVillageDataByInfoIdAndTime(info_id, year, resultParams);
            for (VillageResearch vill : dataItemsList) {
                Map<String, Object> dataMap = new HashMap<String, Object>();
                long num = vill.getLong("num");
                dataMap.put("data", num);
                DecimalFormat df = new DecimalFormat("######0.00");
                double bai = vill.getBigDecimal("bai").setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                String str = df.format(bai);
                dataMap.put("data1", str);
                data.add(dataMap);
            }
            itemsMap.put("type", "bar");
            itemsMap.put("name", (int) Double.parseDouble(resultParams));
            itemsMap.put("data", data);
            seriesList.add(itemsMap);
            i++;
        }
        return seriesList;
    }

    private List<String> getAnalyResult(int info_id) {
        List<VillageResearch> list = showService.getResultByInfo_id(info_id);
        if (list == null || list.size() == 0) {
            return null;
        }
        List<String> resultList = new ArrayList<String>();
        for (VillageResearch vill : list) {
            String result = vill.getStr("result");
            if (!isDecimal(result)) {
                continue;
            }
            int result1 = (int) Double.parseDouble(result);
            if (result1 < 1) {
                continue;
            }
            resultList.add(result);
        }
        return resultList;
    }
}
