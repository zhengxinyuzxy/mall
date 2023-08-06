package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 查询所有首页的一级二级三级信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        List<BaseCategoryView> baseCategoryViewsList = baseCategoryViewMapper.selectList(null);

        Map<Long, List<BaseCategoryView>> baseCategory1Map =
                baseCategoryViewsList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        List<JSONObject> baseCategory1jsonList = new ArrayList<>();

        for (Map.Entry<Long, List<BaseCategoryView>> baseCategory1 : baseCategory1Map.entrySet()) {
            JSONObject jsonObject1 = new JSONObject();

            Long baseCategory1Id = baseCategory1.getKey();

            jsonObject1.put("baseCategory1Id", baseCategory1Id);

            List<BaseCategoryView> baseCategory2List = baseCategory1.getValue();

            jsonObject1.put("baseCategory1Name", baseCategory2List.get(0).getCategory1Name());

            Map<Long, List<BaseCategoryView>> baseCategory2Map =
                    baseCategory2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));

            List<JSONObject> baseCategory2jsonList = new ArrayList<>();

            for (Map.Entry<Long, List<BaseCategoryView>> baseCategory2 : baseCategory2Map.entrySet()) {
                JSONObject jsonObject2 = new JSONObject();

                Long baseCategory2Id = baseCategory2.getKey();

                List<BaseCategoryView> baseCategory3List = baseCategory2.getValue();

                List<JSONObject> baseCategory3jsonList =
                        baseCategory3List.stream().map(baseCategory3 -> {
                            JSONObject jsonObject3 = new JSONObject();

                            jsonObject3.put("baseCategory3Id", baseCategory3.getCategory3Id());
                            jsonObject3.put("baseCategory3Name", baseCategory3.getCategory3Name());

                            return jsonObject3;
                        }).collect(Collectors.toList());

                jsonObject2.put("baseCategory2Id", baseCategory2Id);
                jsonObject2.put("baseCategory2Name", baseCategory3List.get(0).getCategory2Name());
                jsonObject2.put("baseCategory3Child", baseCategory3jsonList);
                baseCategory2jsonList.add(jsonObject2);
            }
            jsonObject1.put("baseCategory2Child", baseCategory2jsonList);

            baseCategory1jsonList.add(jsonObject1);
        }

        return baseCategory1jsonList;

    }
}
