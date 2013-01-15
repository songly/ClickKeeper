package edu.ecnu.clickKeeper.cfg;

/** 
 * @description: Bloom Filter�㷨��ص�����
 * @author: Song Leyi  2012-12-31
 * @version: 1.0
 * @modify: 
 * @Copyright: ����ʦ����ѧ���ѧԺ��Ȩ����
 */
public class BloomFilterCfg
{
    /**
     * Bloom Filter entry size: M
     */
    public static final int ENTRY_SIZE=750;
    
    /**
     * ��ߴ����ٶȣ����ӵ�wraparound counter��C
     */
    public static final int EXPEND_RANGE=10;
    
    /**
     * Bloom Filter��ʹ�õ�ɢ�к����ĸ�����K
     */
    public static final int HASH_NUM=4;
    
    /** 
     * ����Click Stream�����ã�����Timing Bloom Filter��ÿһ��Entry�е�bits size
     * @return
     */
    public static int getEntryBitSize(){
        return (int) Math.ceil(Math.log(ClickStreamCfg.WINDOW_SIZE)/Math.log(2));      
    }
    
    
}
