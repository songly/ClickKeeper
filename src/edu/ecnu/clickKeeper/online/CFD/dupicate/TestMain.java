package edu.ecnu.clickKeeper.online.CFD.dupicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.ecnu.clickKeeper.cfg.BloomFilterCfg;
import edu.ecnu.clickKeeper.cfg.ClickStreamCfg;

/** 
 * @description: �����ɵ������в����ظ���
 * @author: Song Leyi  2013-1-1
 * @version: 1.0
 * @modify: 
 * @Copyright: ����ʦ����ѧ���ѧԺ��Ȩ����
 */
public class TestMain
{

    static int windowSize = ClickStreamCfg.WINDOW_SIZE;    //�������ڴ�С
    static int c = BloomFilterCfg.getEntryBitSize();       //TBF�ṹ�У�ÿһ��ɢ�ж�Ӧ��bit��Ŀ
    static int m = BloomFilterCfg.ENTRY_SIZE;              //TBFɢ�е�Ͱ�ĸ���
    static int k = BloomFilterCfg.HASH_NUM;                //TBF�㷨ʹ�õ�ɢ�к���

    static int copy=0;
    
    static TimingBloomFilter<String> tbf=new TimingBloomFilter<String>(c,m,k);

    /**
     * TBF����������ظ�
     * @param args
     */
    public static void main(String[] args)
    {
        int line=0;      //��¼����
        int numberOfDatasets = 1;                       //��ʹ�����ݼ��ĸ���
        String datasetName = ClickStreamCfg.GEN_PATH;

        System.out.println("Gen Data test Startup: c="+c+"  m="+m+"  k="+k);
        
        File dataset = null;
        BufferedReader buffer = null;       
   
        for (int datasetId = 1; datasetId <= numberOfDatasets; datasetId++)
        {
            datasetName = datasetName+"genLog";
            if (datasetId < 10)
                datasetName = datasetName + "0" + datasetId;
            else
                datasetName = datasetName + datasetId;

            System.out.println("Dataset:" + datasetName);

            dataset=new File(datasetName);
            try
            {
                buffer=new BufferedReader(new FileReader(dataset));
            }
            catch (FileNotFoundException e)
            {
                // TODO ��Ϊlogger.error
                System.out.println("File not found! next...");
                continue;
            }
           
            
            String str=null;
            
            try
            {
                while((str=buffer.readLine())!=null){
                  //  String[] strs=str.split("\\s+");
//                    String element="";
//                    for(int i=0; i<strs.length; i++){
//                        element=element+strs[i];
//                    }
                               
                    DuplicateAlgorithm(str,line);
                    line++;
                }
            }
            catch (IOException e)
            {
                // TODO ��Ϊlogger.error
                e.printStackTrace();
            }
        }

        System.out.println("Gen Data test En: c="+c+"  m="+m+"  k="+k);
        System.out.println("Ideal false positive rate: "+getIdealFPR(m,windowSize,k)); 
        System.out.println("�ظ���:"+(double)copy/(double)line);
    }

    public static void DuplicateAlgorithm(String element, int line)
    {
        int counter=0;   //��¼wrapround counters��Ŀ
        
        counter=line%windowSize+1;
        
        if(line>=windowSize){
            tbf.delete(counter);
        }
        if(tbf.contains(element)){
            copy++;
            
          //  System.out.println("��⵽�ظ�!line:"+line+":"+element.toString());
        }
        tbf.add(element, counter); 
        
        if(line%10000==0){
            System.out.println("Processed: "+line+ " lines");
        }
        
    }
    
    public static double getIdealFPR(int m, int N, int k){
        double ideal=0.0;
        ideal=Math.exp((-(k*N)/(double)m));
        //ideal=Math.pow(Math.E,(-(k*N)/(double)m));
        ideal=1.0-ideal;
        ideal=Math.pow(ideal, k);
        return ideal;
    }

}
