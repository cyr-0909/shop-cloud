package com.fh.shop.cate.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class Cate implements Serializable {

    @ApiModelProperty(value = "分类id",example = "0")
    private Long id;
    @ApiModelProperty(value = "分类名",example = "0")
    private String cateName;
    @ApiModelProperty(value = "分类父id",example = "0")
    private Long fatherId;
    @ApiModelProperty(value = "类型id",example = "0")
    private Long typeId;
    @ApiModelProperty(value = "类型名",example = "0")
    private String typeName;

}
