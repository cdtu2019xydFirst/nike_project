package com.yadong.nike.bigdataanalysis.controller;

import com.yadong.nike.bean.Nikedata;
import com.yadong.nike.bigdataanalysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/29 | 11:46
 **/
@Controller
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @RequestMapping(value = "/{startDay}", method = RequestMethod.GET)
    public String analysis(@PathVariable("startDay") String startDay, Model model, ModelMap modelMap) {
        List<Integer> listnum = new ArrayList<>();
        List<String> listdata = new ArrayList<>();
        List<Integer> newuser = new ArrayList<>();
        List<Integer> newip = new ArrayList<>();
        List<Integer> uv = new ArrayList<>();
        List<Nikedata> nikeAnalyses = analysisService.getOneWeekData(startDay);
        for (Nikedata nikeAnalysis : nikeAnalyses) {
            listnum.add(Integer.valueOf(nikeAnalysis.getPv()));
            listdata.add(nikeAnalysis.getReportTime());
            newuser.add(Integer.valueOf(nikeAnalysis.getNewUser()));
            newip.add(Integer.valueOf(nikeAnalysis.getNewIp()));
            uv.add(Integer.valueOf(nikeAnalysis.getUv()));

        }
        model.addAttribute("infos", listnum);
//        model.addAttribute("time", listdata);
        modelMap.put("time", listdata.get(0));
        model.addAttribute("newuser", newuser);
        model.addAttribute("newip", newip);
        model.addAttribute("uv", uv);

        return "analysis";
    }

}
