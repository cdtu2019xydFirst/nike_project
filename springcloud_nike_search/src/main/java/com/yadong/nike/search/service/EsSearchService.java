package com.yadong.nike.search.service;

import com.yadong.nike.bean.PmsSearchParam;
import com.yadong.nike.bean.PmsSearchSkuInfo;
import com.yadong.nike.bean.PmsSkuAttrValue;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/23 | 15:50
 **/
@Service
@Transactional
public class EsSearchService {

    @Autowired
    private JestClient jestClient;//elasticsearch

    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        String dslStr = getSearchDsl(pmsSearchParam);

        /*用api执行复杂查询语句*/
        Search search = new Search.Builder(dslStr).addIndex("nikepms").addType("PmsSkuInfo").build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            /*====================================================================================================================================*/
            /*由于source与highlight字段同级，并且highlight字段单独在source外，为了达到高亮显示，必须把source里面的“skuName”替换成highlight里面的“skuName”*/
            /*====================================================================================================================================*/
            PmsSearchSkuInfo source = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            String skuName = highlight.get("skuName").get(0);
            source.setSkuName(skuName);
            pmsSearchSkuInfos.add(source);
        }
        System.err.println(pmsSearchSkuInfos.size());
        System.err.println(pmsSearchSkuInfos);
        return pmsSearchSkuInfos;
    }


    private String getSearchDsl(PmsSearchParam pmsSearchParam) {

        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();//属性列表
        String keyword = pmsSearchParam.getKeyword();//关键字
        String catalog3Id = pmsSearchParam.getCatalog3Id();//三级分类Id
        /*query执行工具*/
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //filter
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder1);
        }
        if (skuAttrValueList != null) {
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue.getValueId());
                boolQueryBuilder.filter(termQueryBuilder1);
            }
        }
        //must
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder1);
        }
        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //sort,排序
        searchSourceBuilder.sort("id", SortOrder.DESC);
        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color: red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //装换成JSON格式
        String dslStr = searchSourceBuilder.toString();
        return dslStr;
    }
}
