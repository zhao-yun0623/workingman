package com.workingman.service.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.workingman.javaBean.FoodBean;
import com.workingman.javaBean.InformationBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodListener extends AnalysisEventListener<FoodBean> {
    List<FoodBean> foodList=new ArrayList<>();

    @Override
    public void invoke(FoodBean foodBean, AnalysisContext analysisContext) {
        foodList.add(foodBean);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<FoodBean> getFoodList(){
        return foodList;
    }

}
