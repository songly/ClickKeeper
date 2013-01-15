package edu.ecnu.clickKeeper.cfg;

/** 
 * @description: 与点击日志流相关的配置数据
 * @author: Song Leyi  2012-12-31
 * @version: 1.0
 * @modify: 
 * @Copyright: 华东师范大学软件学院版权所有
 */
public class ClickStreamCfg
{
    /**
     * Sliding Window的窗口大小
     */
    public static final int WINDOW_SIZE=127;
    
    /**
     * 数据的文件路径
     */
    public static final String FILE_PATH="C:/Users/zhenzhen/workspace/ClickKeeper/data/ydata-fp-td-clicks-v2_0.201110";

    /**
     * 生成数据文件的路径
     */
    public static final String GEN_PATH="C:/Users/zhenzhen/workspace/ClickKeeper/data/";
}
