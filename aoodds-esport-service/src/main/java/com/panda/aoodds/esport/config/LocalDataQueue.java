package com.panda.aoodds.esport.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
@Slf4j
public class LocalDataQueue {
   public static ArrayBlockingQueue<String> preMatchBKautoOddsQueue=new ArrayBlockingQueue<String>(10000);
   public static ArrayBlockingQueue<String> preMatchOneKeyBKApplyQueue=new ArrayBlockingQueue<String>(5000);

   public static void putpreMatchBKautoOdds(String aoMatchId){
      if(!preMatchBKautoOddsQueue.contains(aoMatchId)){

      }
      preMatchBKautoOddsQueue.offer(aoMatchId);
   }




}
