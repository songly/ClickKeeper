package edu.ecnu.clickKeeper.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import edu.ecnu.clickKeeper.cfg.ClickStreamCfg;

/** 
 * @description: 生成随机日志
 * @author: Song Leyi  2013-1-1
 * @version: 1.0
 * @modify: 
 * @Copyright: 华东师范大学软件学院版权所有
 */
public class DataGen
{

    /**
     * 生成无关随机日志
     * @param args
     */
    public static void main(String[] args)
    {
        String genFileName="genLog01";
        File genLog=new File(ClickStreamCfg.GEN_PATH+genFileName);
        int lines=1000000;
        
        try{
            if(!genLog.exists()){
                genLog.createNewFile();
            }
            
            BufferedWriter writer=new BufferedWriter(new FileWriter(genLog));
            
            for(int i=0;i<lines;i++){
            String line=generateOneItem();
            writer.write(line);
            
            writer.newLine();
            }
            
            writer.flush();
            writer.close();
            
        }catch(Exception e){
            System.out.println("Error: 生成随机日志记录失败");
        }
        
        System.out.println("生成随机日志成功，生成条数:"+lines);

    }

    private static String generateOneItem()
    {
        //日志中每行记录所包含的字段
        String user="user-";  //随机生成1-1000000的数据，如user-1010
        String age="20";      //生成10-70的数据
        String sex="female";   //生成0或1，0表示female,1表示male
        String nation="86";    //生成1-200之间的国家代码
        String hobby="12345";  //生成100-120000之间的代码
        String delay="123";    //滞留在页面的秒数,1-5000直接
        
        user=user+generateID();
        age=generateAge();
        sex=generateSex();
        nation=generateNation();
        hobby=generateHobby();
        delay=generateDelay();
        
        String log=user+"|"+age+"|"+sex+"|"+nation+"|"+hobby+"|"+delay;
        return log;
    }

    /**
     * 生成区间中随机数核心算法
     * @param min  下限
     * @param max  上限
     * @return
     */
    private static String genNum(int min, int max)
    {
        int num=genNumCore(min,max);
        String m=String.valueOf(num);
        return m;
    }
    /**
     * 生成区间中随机数核心算法
     * @param min  下限
     * @param max  上限
     * @return
     */
    private static int genNumCore(int min, int max)
    {
        Random r=new Random();
        int m=Math.abs(r.nextInt())%(max-min)+min;
        return m;
    }
    
    private static String generateDelay()
    {
        int max=4999;
        int min=1;
        String delay=genNum(min,max);
        return delay;
    }

    private static String generateHobby()
    {
        int max=120000;
        int min=100;
        String hobby=genNum(min,max);
        return hobby;
    }

    private static String generateNation()
    {
        int max=200;
        int min=1;
        String nation=genNum(min,max);
        return nation;
    }

    private static String generateSex()
    {
        int min=1;
        int max=2;
        String sex=(genNumCore(min,max)==1? "male" : "female");
        return sex;
    }

    private static String generateAge()
    {
        int min=10;
        int max=70;
        String age=genNum(min,max);
        return age;
    }

    private static String generateID()
    {
        int min=1;
        int max=1000000;
        String id=genNum(min,max);
        return id;
    }

}
