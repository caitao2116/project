package com.pinyougou.page.service;

public interface ItemPageService {

	/**
	 * 生成商品明细页面
	 * @param goodsId
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);
	
	/**
	 * 删除商品详情页面
	 * @param goodsIds
	 * @return
	 */
	public boolean deleteItemHtml(Long[] goodsIds);
}
