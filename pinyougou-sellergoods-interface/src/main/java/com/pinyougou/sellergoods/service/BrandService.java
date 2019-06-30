package com.pinyougou.sellergoods.service;

import java.util.List;


import com.pinyougou.pojo.TbBrand;
/**
 * 品牌接口
 * @author cai
 *
 */

import entity.PageResult;
import entity.Result;
public interface BrandService {
	//不分页查询所有
	public List<TbBrand> findAll();
	
	/**
	 * 分页查询
	 * @param pageNum 当前页码
	 * @param pageSize 每页展示数据条数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	/**
	 * 新增
	 * @param brand
	 * 
	 */
	public void add(TbBrand brand);
	
	
	/**
	 * 根据id查询单个品牌信息
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);
	
	
	/**
	 * 修改
	 * @param brand
	 */
	public void update(TbBrand brand);
	
	
	/**
	 * 删除选中
	 * @param ids
	 */
	public void delete(Long[] ids);
	
	/**
	 * 条件查询
	 * @param brand 查询条件
	 * @param pageNum 当前页码
	 * @param pageSize 每页展示的条数
	 * @return
	 */
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);
	
}
