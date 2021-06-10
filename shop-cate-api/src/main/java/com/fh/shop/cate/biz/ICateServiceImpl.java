package com.fh.shop.cate.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.cate.mapper.ICateMapper;
import com.fh.shop.cate.po.Cate;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cateService")
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper cateMapper;

    @Override
    public ServerResponse findAllList() {
        String cateListInfo = RedisUtil.get("cateList");
        if (StringUtils.isNoneEmpty(cateListInfo)){
            //从缓存中查找
            //将json格式字符串转换为java对象
            List<Cate> cateList=JSON.parseArray(cateListInfo, Cate.class);
            //直接返回
            return ServerResponse.success(cateList);
        }
        //从数据库中查找
        List<Cate> cateList=cateMapper.selectList(null);
        //将java对象转换为json格式
        String cateListJson = JSON.toJSONString(cateList);
        //将数据放入缓存中
        RedisUtil.setEx("cateList",cateListJson,30);

        return ServerResponse.success(cateList);
    }

}
