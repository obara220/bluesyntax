package com.panda.aoodds.esport.common.utils;


import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 集合容器工具类
 * @Author carson
 * @DATE 2023/6/01 14:16
 **/
@Slf4j
public class AoCollectionUtils {
    /**
     * data：需要分批的数据
     * sublimit:没批数量
     * 如果数据数量大于 subLimit*16 的数据量则分批失败，需要增加并行处理的分批数量（该增加不会对性能产生较大影响，不会对其他引用产生影响）
     */

    public static <T> List<List<T>> subList(List<T> data,Integer subLimit){
        Integer dataSize = data.size();
        if(dataSize<=subLimit){
            return Arrays.asList(data);
        }
        if(dataSize>subLimit*52){
            log.error("批量分割失败,分配的批次值过小");
            return Arrays.asList(data);
        }
        Integer lastBatchTimes = dataSize%subLimit==0?0:1;
       Integer batchTimes = (dataSize/subLimit)+lastBatchTimes;
        return   Arrays.asList(0,subLimit,subLimit*2,subLimit*3,subLimit*4,subLimit*5,subLimit*6,subLimit*7,subLimit*8,subLimit*9,subLimit*10,subLimit*11,subLimit*12,subLimit*13,subLimit*14,subLimit*15,subLimit*16,subLimit*17,subLimit*18,subLimit*19,subLimit*20,subLimit*21,subLimit*22,subLimit*23,subLimit*24,subLimit*25,subLimit*26,subLimit*27,subLimit*28,subLimit*29,subLimit*30,subLimit*31,
                        subLimit*32,subLimit*33,subLimit*34,subLimit*35,subLimit*36,subLimit*37,subLimit*38,subLimit*39,subLimit*40,subLimit*41,subLimit*42,subLimit*43,subLimit*44,subLimit*45,subLimit*46,subLimit*47,subLimit*48,subLimit*49,subLimit*50,subLimit*51,subLimit*52)
                .parallelStream().filter(f->batchTimes*subLimit>f).map(f->{
                    if(dataSize<subLimit+f){
                        return data.subList(f,f+(dataSize-f));
                    }
                    return data.subList(f,subLimit+f);
                }).collect(Collectors.toList());
    }
    public static Integer[] periodConvert(Integer code){
        Integer[] period=new Integer[2];
                switch (code){
                    case 0:period[0]=4;period[1]=10;break;
                    case 7:period[0]=4;period[1]=12;break;
                    case 17:period[0]=2;period[1]=20;break;
                    case 64:period[0]=4;period[1]=6;break;
                    case 68:period[0]=4;period[1]=5;break;
                    case 70:period[0]=4;period[1]=4;break;
                    case 73:period[0]=4;period[1]=10;break;
                    default:period[0]=4;period[1]=10;break;
                }
                return period;
        }
}
