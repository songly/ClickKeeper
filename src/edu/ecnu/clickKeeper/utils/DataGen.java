package edu.ecnu.clickKeeper.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import edu.ecnu.clickKeeper.cfg.ClickStreamCfg;

/** 
 * @description: ���������־
 * @author: Song Leyi  2013-1-1
 * @version: 1.0
 * @modify: 
 * @Copyright: ����ʦ����ѧ���ѧԺ��Ȩ����
 */
public class DataGen
{

    /**
     * �����޹������־
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
            System.out.println("Error: ���������־��¼ʧ��");
        }
        
        System.out.println("���������־�ɹ�����������:"+lines);

    }

    private static String generateOneItem()
    {
        //��־��ÿ�м�¼���������ֶ�
        String user="user-";  //�������1-1000000�����ݣ���user-1010
        String age="20";      //����10-70������
        String sex="female";   //����0��1��0��ʾfemale,1��ʾmale
        String nation="86";    //����1-200֮��Ĺ��Ҵ���
        String hobby="12345";  //����100-120000֮��Ĵ���
        String delay="123";    //������ҳ�������,1-5000ֱ��
        
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
     * ��������������������㷨
     * @param min  ����
     * @param max  ����
     * @return
     */
    private static String genNum(int min, int max)
    {
        int num=genNumCore(min,max);
        String m=String.valueOf(num);
        return m;
    }
    /**
     * ��������������������㷨
     * @param min  ����
     * @param max  ����
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
