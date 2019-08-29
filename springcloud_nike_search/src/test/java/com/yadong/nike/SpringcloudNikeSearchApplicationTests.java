package com.yadong.nike;

import com.yadong.nike.bean.PmsSearchSkuInfo;
import com.yadong.nike.bean.PmsSkuInfo;
import com.yadong.nike.search.testFeign.TestSkuFeignService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableFeignClients(basePackages = "com.yadong.nike.search.testFeign")
@EnableDiscoveryClient
public class SpringcloudNikeSearchApplicationTests {

    @Autowired
    private TestSkuFeignService testSkuFeignService;

    @Autowired
    private JestClient jestClient;

    /**
     * 将mysql数据导入elasticsearch
     *
     * @throws IOException
     */

    @Test
    public void contextLoads() throws IOException {
        /*查询mysql数据*/
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();
        pmsSkuInfoList = testSkuFeignService.getSkuInfo();
        /*转化为es结构*/
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfo.setPrice(pmsSkuInfo.getPrice().toString());//定义价格是类型不一致问题
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }
        /*将数据导入es*/
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
        /*Index.Builder(null).index(null).type().id().build();
                    （数据） （库名）    （表名） （主键）*/
            Index put = new Index.Builder(pmsSearchSkuInfo).index("nike").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()).build();
            /*上面相当于elasticsearch的put语句，下面是执行该语句*/
            jestClient.execute(put);
        }
    }

}
