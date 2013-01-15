package edu.ecnu.clickKeeper.utils;

import edu.ecnu.clickKeeper.cfg.BloomFilterCfg;
import edu.ecnu.clickKeeper.cfg.ClickStreamCfg;
import edu.ecnu.clickKeeper.online.CFD.dupicate.BloomFilter;
import edu.ecnu.clickKeeper.online.CFD.dupicate.TimingBloomFilter;

public class Test
{

    /**
     * Test
     * @param args
     */
    public static void main(String[] args)
    {
        
        int N=ClickStreamCfg.WINDOW_SIZE;
        int m=550;
        int k=3;
            
        System.out.println(getM(3,N));
        System.out.println(getM(4,N));
        System.out.println(getM(5,N));
        System.out.println(getM(6,N));
        System.out.println(getM(7,N));
        System.out.println(getM(8,N));
        
            System.out.println(getIdealFPR( m, N, k));
            System.out.println(getIdealFPR( 750, N, 4));
            System.out.println(getIdealFPR( 900, N, 5));
            System.out.println(getIdealFPR( 1100, N, 6));
            System.out.println(getIdealFPR( 1300, N, 7));
            System.out.println(getIdealFPR( 1500, N, 8));
        // ²âÊÔentry bit sizeµÄCeil·½·¨
//        System.out.println(BloomFilterCfg.getEntryBitSize());
//        
//        double falsePositiveProbability=0.01;
//        int expectedSize=10000;
//        
//        TimingBloomFilter<String> bf=new TimingBloomFilter<String>(falsePositiveProbability,expectedSize);
//       // TimingBloomFilter<String> bf=new TimingBloomFilter<String>(4,100,5);
//       // BloomFilter<String> bf=new BloomFilter<String>(falsePositiveProbability,expectedSize);
//        
//       // bf.add("foo", 1);
//        bf.add("foo",1);
//        System.out.println(bf.getBitSet().toString());
//        bf.add("bar",2);
//        System.out.println(bf.getBitSet().toString());
//        bf.add("guess",3);
//        System.out.println(bf.getBitSet().toString());
//        
//        if(bf.contains("foo")){
//            System.out.println("Foo in");
//            
//        }
//        
//        if(bf.contains("bar")){
//            System.out.println("bar in");
//            
//        }
//        if(bf.contains("foooooooooooooooooookk")){
//            System.out.println("foooooooooooooooooookk in");
//            
//        }
//        if(bf.contains("guess")){
//            System.out.println("guess in");
//            
//        }
//        
//        if(bf.contains("fdaf")){
//            System.out.println("fdaf in");
//            
//        }
//        
//        bf.delete(3);
//        System.out.println(bf.getBitSet().toString());
//        
//        if(bf.contains("foo")){
//            System.out.println("Foo in");
//            
//        }
//        if(bf.contains("guess")){
//            System.out.println("guess in");
//            
//        }
//        
//        if(bf.contains("bar")){
//            System.out.println("bar in");
//            
//        }
//        if(bf.contains("dfadfdf")){
//            System.out.println("dfadfdf in");
//            
//        }
//        if(bf.contains("fdaf")){
//            System.out.println("fdaf in");
//            
//        }
        
        
        

    }
    
    public static double getIdealFPR(int m, int N, int k){
        double ideal=0.0;
        ideal=Math.exp((-(k*N)/(double)m));
        //ideal=Math.pow(Math.E,(-(k*N)/(double)m));
        ideal=1.0-ideal;
        ideal=Math.pow(ideal, k);
        return ideal;
    }
    
    public static double getM(int k,int N){
        double m=0.0;
        
        m=k*(double)N/(Math.log(2));
        
        return m;
    }

}
