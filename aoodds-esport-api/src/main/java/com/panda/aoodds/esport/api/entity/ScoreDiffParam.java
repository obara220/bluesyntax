package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;
@Data
public class ScoreDiffParam implements Serializable {
    private Integer diff;
    private Float adjust;
}
