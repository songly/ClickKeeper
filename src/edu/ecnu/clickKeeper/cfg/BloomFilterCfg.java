package edu.ecnu.clickKeeper.cfg;

/** 
 * @description: Bloom Filter算法相关的配置
 * @author: Song Leyi  2012-12-31
 * @version: 1.0
 * @modify: 
 * @Copyright: 华东师范大学软件学院版权所有
 */
public class BloomFilterCfg
{
    /**
     * Bloom Filter entry size: M
     */
    public static final int ENTRY_SIZE=750;
    
    /**
     * 提高处理速度，增加的wraparound counter：C
     */
    public static final int EXPEND_RANGE=10;
    
    /**
     * Bloom Filter中使用的散列函数的个数：K
     */
    public static final int HASH_NUM=4;
    
    /** 
     * 根据Click Stream的配置，返回Timing Bloom Filter中每一个Entry中的bits size
     * @return
     */
    public static int getEntryBitSize(){
        return (int) Math.ceil(Math.log(ClickStreamCfg.WINDOW_SIZE)/Math.log(2));      
    }
    
    
}
