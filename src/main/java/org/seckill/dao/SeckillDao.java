package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
/**
 * 
 * @author Administrator
 *
 */
public interface SeckillDao {
/**
 * 减库存
 */
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	/**
	 * 根据id查询秒杀对象
	 * @param seckillId
	 * @return 如果影响行数>1，表示更新的记录行数
	 */
	Seckill queryById(long seckillId);
	/**
	 * 根据偏移量查询秒杀商品列表
	 * @param offet
	 * @param limit
	 * @return
	 * java没有保存形参的记录queryAll(offset,limit)->queryAll(args0,args1)
	 */
	List<Seckill> queryAll(@Param("offset")int offset,@Param("limit")int limit);
	/**
	 * 使用存储过程执行秒杀
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}
