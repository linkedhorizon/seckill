package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jca.context.ResourceAdapterApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/seckill")// url:模块/资源/{id}/细分 /seckill/list
public class SeckillController {
	private final Logger logger=LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//获取列表页
		List<Seckill> list=seckillService.getSeckillList();
		model.addAttribute("list",list);
		//list.jsp+model=ModelAndView
		return "list";// WEB-INF/jsp/list.jsp
	}

	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId,Model model){
		if(seckillId==null){
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",seckill);
		return "detail";
	}
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
		SeckillResult<Exposer> result;
		try{
			Exposer exposer=seckillService.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true,exposer);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result=new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method = RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExcution> excute(@PathVariable("seckillId") Long seckillId,
			@PathVariable("md5") String md5,
			@CookieValue(value="killPhone",required=false)Long userPhone){
		if(userPhone==null){
			return new SeckillResult<SeckillExcution>(false,"未注册");
		}
		
		try{
			//SeckillExcution excution=seckillService.excuteSeckill(seckillId, userPhone, md5);
			//通过存储过程调用
			SeckillExcution excution=seckillService.excuteSeckillProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExcution>(true,excution);
		}catch(RepeatKillException e1){
			
			SeckillExcution excution=new SeckillExcution(seckillId,SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExcution>(true,excution);
		}
		catch(SeckillCloseException e2){
			SeckillExcution excution=new SeckillExcution(seckillId,SeckillStateEnum.END);
			return new SeckillResult<SeckillExcution>(true,excution);
		}
		catch(Exception e){
			logger.error(e.getMessage(),e);
			SeckillExcution excution=new SeckillExcution(seckillId,SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExcution>(true,excution);
		}
		
	}
	@RequestMapping(value="/time/now", method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date date=new Date();
		return new SeckillResult<Long>(true,date.getTime());
	}
}
