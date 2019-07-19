package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;



@Component
public class SolrUtil {
	
	@Autowired
	private TbItemMapper tbItemMapper;
	
	//导入solr模板
	@Autowired
	private SolrTemplate solrTemplate;
	
	public void importItemData() {
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andAuditStatusEqualTo("1");
		List<TbItem> itemList = tbItemMapper.selectByExample(example);
		System.out.println("---商品列表---");
		for(TbItem item:itemList) {
			Map specMap = JSON.parseObject(item.getSpec(),Map.class);
			item.setSpecMap(specMap);
			System.out.println("标题："+item.getTitle()+"价格： "+item.getPrice());
			
		}
		System.out.println("总条数: "+itemList.size());
		
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		
		System.out.println("---结束---");
	}
	
	
	public static void main(String[] args)  {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		solrUtil.importItemData();
	}

}
