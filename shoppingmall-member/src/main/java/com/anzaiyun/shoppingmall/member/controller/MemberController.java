package com.anzaiyun.shoppingmall.member.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.anzaiyun.shoppingmall.member.exception.PhoneExsitException;
import com.anzaiyun.shoppingmall.member.exception.UserNameExsitException;
import com.anzaiyun.shoppingmall.member.feign.coupon.CouponFeignService;
import com.anzaiyun.shoppingmall.member.vo.UserLoginVo;
import com.anzaiyun.shoppingmall.member.vo.UserRegistVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anzaiyun.shoppingmall.member.entity.MemberEntity;
import com.anzaiyun.shoppingmall.member.service.MemberService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.R;



/**
 * 会员
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 20:52:53
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }



    @RequestMapping("/regist")
    public R regist(@RequestBody UserRegistVo userRegistVo){
        memberService.regist(userRegistVo);

        return R.ok();
    }

    /**
     * 注册前校验
     * @param userRegistVo
     * @return
     */
    @RequestMapping("/beforeRegist")
    public Map<String,String> beforeRegist(@RequestBody UserRegistVo userRegistVo){
        Map<String,String> errors = new HashMap<>();

        //这里是查询出结果后对结果数据进行判断，其实也可以直接采用异常的方式将异常抛出
        MemberEntity memberEntity = memberService.getOne(new QueryWrapper<MemberEntity>().eq("username", userRegistVo.getUName()).or().
                eq("mobile", userRegistVo.getUPhone()));

        if (memberEntity!=null){
            if (memberEntity.getUsername().equals(userRegistVo.getUName())){
                errors.put("uName","当前用户名已存在请重新填写");
            }

            if (memberEntity.getMobile().equals(userRegistVo.getUPhone())){
                errors.put("uPhone","当前手机号码已注册，请直接登录");
            }
        }

        //使用捕获异常的方式来校验信息，感觉这种方式好像也没有多实用。。。
//        try{
//            memberService.checkPhoneUnique(userRegistVo.getUPhone());
//        }catch (PhoneExsitException e){
//            errors.put("uPhone","当前手机号码已注册，请直接登录");
//        }
//
//        try {
//            memberService.checkUserNameUnique(userRegistVo.getUName());
//        }catch (UserNameExsitException e){
//            errors.put("uName","当前用户名已存在请重新填写");
//        }



        return errors;
    }

    /**
     * 登录前校验
     * @param userLoginVo
     * @return
     */
    @RequestMapping("/beforeLogin")
    public Map<String,String> beforeLogin(@RequestBody UserLoginVo userLoginVo){

        Map<String,String> errors = memberService.checkLoginUser(userLoginVo);

        return errors;
    }

    @RequestMapping("/membercouponslist")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("泰妍");

        R memberCoupons = couponFeignService.memberCoupons();

        return R.ok().put("memeber",memberEntity).put("coupons",memberCoupons.get("coupons"));
    }

}
