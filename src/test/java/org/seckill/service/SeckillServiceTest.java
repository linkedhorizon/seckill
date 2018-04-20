package org.seckill.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SeckillService seckillService;
	@Test
	public void testGetSeckillList() {
		List<Seckill> seckills=seckillDao.queryAll(0, 10);
		logger.info("seckills={}",seckills);
	}

	@Test
	public void testGetById() {
		Seckill seckill=seckillDao.queryById(1000L);
		logger.info("seckill={}",seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		Exposer exposer=seckillService.exportSeckillUrl(1000L);
		logger.info("exposer={}",exposer);
	}

	@Test
	public void testExcuteSeckill() {
		long id=1000;
		long phone=18264335555L;
		String md5="dgj0a6fa0afm85d00gb";
		try{
			SeckillExcution excution=seckillService.excuteSeckill(id, phone, md5);
			logger.info("excution={}",excution);
		}catch(SeckillCloseException e1){
			logger.error(e1.getMessage());
		}catch(RepeatKillException e2){
			logger.error(e2.getMessage());
		}
		catch(SeckillException e){
			logger.error(e.getMessage());
		}
		
		
	}

}
