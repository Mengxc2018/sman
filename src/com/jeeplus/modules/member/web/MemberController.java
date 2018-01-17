/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.member.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.member.entity.Member;
import com.jeeplus.modules.member.service.MemberService;
import com.jeeplus.modules.sys.service.SystemService;

/**
 * 会员管理Controller
 * @author zhaoliangdong
 * @version 2017-10-15
 */
@Controller
@RequestMapping(value = "${adminPath}/member/member")
public class MemberController extends BaseController {

	@Autowired
	private MemberService memberService;
	
	@ModelAttribute
	public Member get(@RequestParam(required=false) String id) {
		Member entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = memberService.get(id);
		}
		if (entity == null){
			entity = new Member();
		}
		return entity;
	}
	
	/**
	 * 会员信息列表页面
	 */
	@RequiresPermissions("member:member:list")
	@RequestMapping(value = {"list", ""})
	public String list(Member member, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Member> page = memberService.findPage(new Page<Member>(request, response), member); 
		model.addAttribute("page", page);
		return "modules/member/memberList";
	}

	/**
	 * 查看，增加，编辑会员信息表单页面
	 */
	@RequiresPermissions(value={"member:member:view","member:member:add","member:member:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Member member, Model model) {
		model.addAttribute("member", member);
		return "modules/member/memberForm";
	}

	/**
	 * 保存会员信息
	 */
	@RequiresPermissions(value={"member:member:add","member:member:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Member member, Model model, RedirectAttributes redirectAttributes) throws Exception{
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(member.getNewPassword())) {
			member.setPassword(SystemService.entryptPassword(member.getNewPassword()));
		}
		if(!member.getIsNewRecord()){//编辑表单保存
			Member t = memberService.get(member.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(member, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			memberService.save(t);//保存
		}else{//新增表单保存
			memberService.save(member);//保存
		}
		addMessage(redirectAttributes, "保存会员信息成功");
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
	}
	
	/**
	 * 删除会员信息
	 */
	@RequiresPermissions("member:member:del")
	@RequestMapping(value = "delete")
	public String delete(Member member, RedirectAttributes redirectAttributes) {
		memberService.delete(member);
		addMessage(redirectAttributes, "删除会员信息成功");
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
	}
	
	/**
	 * 批量删除会员信息
	 */
	@RequiresPermissions("member:member:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberService.delete(memberService.get(id));
		}
		addMessage(redirectAttributes, "删除会员信息成功");
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
	}
	
	/**
	 * 密码重置
	 */
	@RequiresPermissions("member:member:edit")
	@RequestMapping(value = "resetPassword")
	public String resetPassword(Member member, RedirectAttributes redirectAttributes) throws Exception{
		member.setPassword(SystemService.entryptPassword("666666"));
		memberService.resetPassword(member);//保存
		addMessage(redirectAttributes, "密码重置成功,新密码为：666666");
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("member:member:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Member member, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "会员信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Member> page = memberService.findPage(new Page<Member>(request, response, -1), member);
    		new ExportExcel("会员信息", Member.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出会员信息记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("member:member:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Member> list = ei.getDataList(Member.class);
			for (Member member : list){
				try{
					memberService.save(member);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条会员信息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条会员信息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入会员信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
    }
	
	/**
	 * 下载导入会员信息数据模板
	 */
	@RequiresPermissions("member:member:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "会员信息数据导入模板.xlsx";
    		List<Member> list = Lists.newArrayList(); 
    		new ExportExcel("会员信息数据", Member.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/member/member/?repage";
    }
	
	
	

}