package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.fegin.ItemFeign;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping("/page/item")
public class ItemController {

    @Autowired
    private ItemFeign itemFeign;

    /**
     * 商品详情页打开
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping
    public String ItemPage(Long skuId, Model model) {
        // 远程调用查询商品详情的所有数据
        Map<String, Object> skuItem = itemFeign.getSkuItem(skuId);
        // 将数据存入model中供页面展示
        model.addAllAttributes(skuItem);
        return "item";
    }

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 生成静态模板页面
     *
     * @param skuId
     * @return
     */
    @GetMapping("/createItemHtml")
    @ResponseBody
    public String createItemHtml(Long skuId) throws Exception {
        // 远程调用查询商品详情所有数据
        Map<String, Object> skuItem = itemFeign.getSkuItem(skuId);
        // 创建上下文
        Context context = new Context();
        context.setVariables(skuItem);
        File file = new File("D:/" + skuId + ".html");
        PrintWriter printWriter = new PrintWriter(file);
        // 基于模板生成静态页面
        templateEngine.process("item", context, printWriter);
        printWriter.close();
        return "success";
    }

    @Autowired
    private ListFeign listFeign;

    /**
     * 商品的搜索页面
     *
     * @return
     */
    @GetMapping("/list")
    public String listPage(@RequestParam Map<String, String> searchData, Model model) {
        // 远程调用list的微服务，拿到匹配的搜索数据
        Map<String, Object> search = listFeign.search(searchData);
        // 添加入model中，提供前端页面数据匹配
        model.addAllAttributes(search);
        // 关键字搜索回显
        model.addAttribute("searchData", searchData);

        // 获取当前的URL
        String url = getUrl(searchData);
        model.addAttribute("url", url);
        System.out.println(url);
        // 获取页码
        Object page = search.get("page");
        Object total = search.get("total");
        Object size = search.get("size");
        Page<Object> pageInfo = new Page<>(Long.parseLong(total.toString()),
                Integer.parseInt(page.toString()),
                Integer.parseInt(size.toString()));
        model.addAttribute("pageInfo", pageInfo);
        // 下一页页码
        pageInfo.getNext();
        // 上一页页码
        pageInfo.getUpper();
        return "list";
    }

    /**
     * 获取URL
     *
     * @param searchData
     */
    private String getUrl(Map<String, String> searchData) {
        String url = "/page/item/list?";
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("page") && !key.equals("highField") && !key.equals("lowField")) {
                String value = entry.getValue();
                url = url + key + "=" + value + "&";

            }
        }
        return url.substring(0, url.length() - 1);
    }
}
