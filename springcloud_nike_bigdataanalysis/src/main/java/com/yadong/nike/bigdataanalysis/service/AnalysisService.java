package com.yadong.nike.bigdataanalysis.service;

import com.yadong.nike.bean.Nikedata;
import com.yadong.nike.bigdataanalysis.mapper.NikeAnalysisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/29 | 13:10
 **/
@Service
@Transactional
public class AnalysisService {

    @Autowired
    private NikeAnalysisMapper nikeAnalysisMapper;

    public List<Nikedata> getOneWeekData(String startDay) {
        Nikedata nikedata = new Nikedata();
        List<Nikedata> nikedataListParam = new ArrayList<>();
        nikedata.setReportTime(startDay);
        List<Nikedata> nikedataList = nikeAnalysisMapper.selectAll();
        int count = 7;
        for (Nikedata nikedata1 : nikedataList) {
            if (startDay != null && startDay.equals(nikedata1.getReportTime())){
                nikedataListParam.add(nikedata1);
                count --;
            }else if (count != 7 && count > 0){
                nikedataListParam.add(nikedata1);
                count --;
            }
        }
        return nikedataListParam;
    }
}
