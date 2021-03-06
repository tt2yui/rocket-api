package com.github.alenfive.rocketapi;

import com.github.alenfive.rocketapi.entity.ApiParams;
import com.github.alenfive.rocketapi.extend.IApiPager;
import com.github.alenfive.rocketapi.service.ScriptParseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

/**
 * 数据为操作解析器测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={RocketAPIApplication.class})// 指定启动类
@Slf4j
public class ApplicationTests {

    @Autowired
    private ScriptParseService parseService;

    @Test
    public void testVar() {
        StringBuilder script  = new StringBuilder("from ${table} where id in = #{id} and name=#{name123456789}#{name123456789}");
        ApiParams apiParams = new ApiParams();
        apiParams.putParam("id","123");
        apiParams.putParam("table","t_user");
        apiParams.putParam("name123456789"," and #{phone}");
        parseService.buildParams(script,apiParams,null);
        log.info("testVar:{}",script);
        assert script.toString().equals("from t_user where id in = '123' and name=' and #{phone}'' and #{phone}'");
    }

    @Test
    public void testArrayVar(){
        StringBuilder script  = new StringBuilder("where id in (#{idList})");
        ApiParams apiParams = new ApiParams();
        apiParams.putParam("idList", Arrays.asList("11",22));
        parseService.buildParams(script,apiParams,null);
        log.info("testFor:{}",script);
        assert script.toString().equals("where id in ('11',22)");
    }

    @Test
    public void testRandomArrayVar(){
        StringBuilder script  = new StringBuilder("where id = #{idList[1].name}");
        ApiParams apiParams = new ApiParams();
        Map<String,Object> child = new HashMap<>();
        child.put("name","王");

        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add(child);
        apiParams.putParam("idList", list);
        parseService.buildParams(script,apiParams,null);
        log.info("testRandomArrayVar:{}",script);
        assert script.toString().equals("where id = '王'");
    }

    @Test
    public void testIf(){
        StringBuilder script  = new StringBuilder("where ?{id,and id=#{id}} and 1=1");
        ApiParams apiParams = new ApiParams();
        apiParams.putParam("id","123");
        parseService.buildIf(script,apiParams);
        log.info("testIf:{}",script.toString());
        assert script.toString().equals("where and id=#{id} and 1=1");

        script  = new StringBuilder("where ?{id,and id=#{id}} and 1=1");
        apiParams = new ApiParams();
        parseService.buildIf(script,apiParams);
        log.info("testIf:{}",script.toString());
        assert script.toString().equals("where  and 1=1");
    }

    @Test
    public void testFor(){
        List<Map<String,Object>> ss = new ArrayList<>();

    }


    @Autowired
    private IApiPager apiPager;

    @Test
    public void testPager(){
        StringBuilder script = new StringBuilder("select * from user limit #{index},#{pageSize}");

        ApiParams apiParams = new ApiParams();
        apiParams.putParam("pageNo",2);

        Integer pageNo = null;
        Object value = parseService.buildParamItem(apiParams,apiPager.getPageNoVarName());
        if (StringUtils.isEmpty(value)){
            apiParams.putParam(apiPager.getPageNoVarName(),apiPager.getPageNoDefaultValue());
            pageNo = apiPager.getPageNoDefaultValue();
        }else {
            pageNo = Integer.valueOf(value.toString());
        }

        Integer pageSize = null;

        value = parseService.buildParamItem(apiParams,apiPager.getPageSizeVarName());
        if (StringUtils.isEmpty(value)){
            apiParams.putParam(apiPager.getPageSizeVarName(),apiPager.getPageSizeDefaultValue());
            pageSize = apiPager.getPageSizeDefaultValue();
        }else{
            pageSize =Integer.valueOf(value.toString());

        }

        apiParams.putParam(apiPager.getIndexVarName(),apiPager.getIndexVarValue(pageSize,pageNo));
        parseService.buildParams(script,apiParams,null);
        log.info("testPager:{}",script.toString());
        assert script.toString().equals("select * from user limit 15,15");

    }

    @Test
    public void jsScript() throws ScriptException {
        String str = "4+5";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        System.out.println(engine.eval(str));
    }
}