package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String,Object> map = new HashMap<>();
		
		//去除搜索框中的空格
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replaceAll(" ", ""));
		
		//缓存关键词
		String str = (String) searchMap.get("keywords");
		if ("".equals(str)) {//前端的关键词为空
			//把缓存中的关键词赋给搜索条件
			str = redisTemplate.opsForValue().get("keywords").toString();
			searchMap.put("keywords", str);
		}else {//前端的关键词不为空
			//更新缓存
			redisTemplate.opsForValue().set("keywords", (String) searchMap.get("keywords"));
		}
		
		
		/*Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query , TbItem.class);*/
//		System.out.println(searchMap);
		//添加搜索集合
		map.putAll(searchList(searchMap));
		
		//添加分类集合
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		//根据分类来确定品牌和规格列表
		String categoeyName = (String) searchMap.get("category");
		if (!"".equals(categoeyName)) {//选择了分类
			map.putAll(searchBrandAndSpecList(categoeyName));
		}else {//没有选择分类
			if(categoryList.size()>0 && categoryList!=null) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		
		
//		System.out.println(map);
		
		return map;
	}
	
	/**
	 * 生成搜索列表
	 * @param searchMap
	 * @return
	 */
	private Map searchList(Map searchMap) {
		Map<String,Object> map = new HashMap<>();
		HighlightQuery query = new SimpleHighlightQuery();
		//声明高亮显示的域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		//添加前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		//添加后缀
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions );
		
		/*Criteria criteria = null;
		query.addCriteria(criteria);*/
		
		//关键字过滤条件
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		/*if(searchMap.get("keywords") != null && !"".equals(searchMap.get("keywords"))) {
			FilterQuery filterquery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
			filterquery.addCriteria(filterCriteria);
			query.addFilterQuery(filterquery );
		}*/
		
		
		//按照分类过滤
		if(searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
			FilterQuery filterquery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterquery.addCriteria(filterCriteria);
			query.addFilterQuery(filterquery );
		}
		
		//按照品牌过滤
		if(searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {
			FilterQuery filterquery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterquery.addCriteria(filterCriteria);
			query.addFilterQuery(filterquery );
		}
		
		//按照规格过滤
		if(searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for(String key:specMap.keySet()) {
				FilterQuery filterquery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				filterquery.addCriteria(filterCriteria);
				query.addFilterQuery(filterquery );
			}
		}
		
		//按照价格过滤
		if(searchMap.get("price") != null && !"".equals(searchMap.get("price"))) {
			String priceStr = (String) searchMap.get("price");//1000-2000
			String[] price = priceStr.split("-");
			if(!"0".equals(price[0])) {
				FilterQuery filterquery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				filterquery.addCriteria(filterCriteria);
				query.addFilterQuery(filterquery );
			}
			if(!"*".equals(price[1])) {
				FilterQuery filterquery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				filterquery.addCriteria(filterCriteria);
				query.addFilterQuery(filterquery );
			}
		}
		
		//分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo");
		if (pageNo == null) {
			pageNo = 1;
		}
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if (pageSize == null) {
			pageSize = 20;
		}
		query.setOffset((pageNo-1) * pageSize);//分页起始索引
		query.setRows(pageSize);//每页显示条数
		
		
		//排序
		String sortValue = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if (!"".equals(sortValue) && sortValue != null) {
			if("ASC".equals(sortValue)) {
				Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}
			if("DESC".equals(sortValue)) {
				Sort sort = new Sort(Sort.Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query , TbItem.class);
		List<HighlightEntry<TbItem>> highlightedList = page.getHighlighted();
		for(HighlightEntry<TbItem> h : highlightedList) {
			TbItem item = h.getEntity();
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0) {
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		
		return map;
	}

	/**
	 * 查询分类列表
	 * @param searchMap
	 * @return
	 */
	private List<String> searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();
		
		//设置查询条件
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		//设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions );
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query , TbItem.class);
		//获得分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//获取分组入口集合
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		
		for(GroupEntry<TbItem> entity:content) {
			list.add(entity.getGroupValue());
		}
		
		return list;
	}
	
	
	
	/**
	 * 品牌和规格列表
	 * @param searchMap
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId != null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
		
	}

	@Override
	public void deleteByGoodsIds(List goodsIds) {
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria );
		solrTemplate.delete(query );
		solrTemplate.commit();
		
	}
}
