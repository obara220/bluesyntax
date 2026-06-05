package com.panda.aoodds.esport.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 主客队比分实体
 *
 * @param <T> 比分数据类型
 * @author Samuel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAwayScore<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主队比分
     */
    private T homeScore;

    /**
     * 客队比分
     */
    private T awayScore;
}
