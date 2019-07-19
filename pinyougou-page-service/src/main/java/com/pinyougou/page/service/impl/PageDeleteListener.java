package com.pinyougou.page.service.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

/**
 * 商品详情页的删除
 * @author cai
 *
 */
@Component
public class PageDeleteListener implements MessageListener {

	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		
		try {
			ObjectMessage objectMessage = (ObjectMessage)message;
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			boolean b = itemPageService.deleteItemHtml(goodsIds);
			System.out.println("页面删除结果："+b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
