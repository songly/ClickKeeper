package edu.ecnu.clickKeeper.online.CFD.dupicate;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collection;

import edu.ecnu.clickKeeper.cfg.BloomFilterCfg;
import edu.ecnu.clickKeeper.cfg.ClickStreamCfg;

/**
 * @description: TBF算法实现
 * @author: Song Leyi 2012-12-31
 * @version: 1.0
 * @modify:
 * @Copyright: 华东师范大学软件学院版权所有
 */
public class TimingBloomFilter<E> implements Serializable
{
    private static final long serialVersionUID = -7487190129254602560L;

    //bit
    private BitSet bitset;

    //总共的bit位数，包括entry个数*每个entrybit的位数   (区别于经典的Bloom Filter算法)
    private int bitSetSize=BloomFilterCfg.ENTRY_SIZE*BloomFilterCfg.getEntryBitSize();
    //entry个数，相当于经典Bloom Filter的总共bit size
    private int entrySize=BloomFilterCfg.ENTRY_SIZE;

    private int k = BloomFilterCfg.HASH_NUM; //number of hash functions

    private int expectedNumberOfFilterElements; // expected (maximum) number of elements to be added

    private int numberOfAddedElements; // number of elements actually added to the Bloom filter

    static final Charset charset = Charset.forName("UTF-8"); //encoding used for storing hash values as strings
    static final String hashName = "MD5";
    static final MessageDigest digestFunction;
    static
    { //The digest method is reused between instances
        MessageDigest tmp;
        try
        {
            tmp = java.security.MessageDigest.getInstance(hashName);
        }
        catch (NoSuchAlgorithmException e)
        {
            tmp = null;
        }
        digestFunction = tmp;
    }
    
    /**
     * 构造函数  Bloom filter的总长度为 c*m
     * 
     * @param c 每一个entry/元素所占bit的个数
     * @param m filter的entry/Element个数
     * @param k 使用的hash函数的个数
     */
    public TimingBloomFilter(int c, int m, int k){
        this.expectedNumberOfFilterElements=c;
        this.k=k;
        this.setEntrySize(m);
        this.bitSetSize=(int)Math.ceil(c*m);
        numberOfAddedElements=0;
        this.bitset=new BitSet(bitSetSize);
         
    }
    
    /**
     * Constructs an empty Bloom filter. The optimal number of hash functions (k) is estimated from the total size of the Bloom
     * and the number of expected elements.
     *
     * @param bitSetSize defines how many bits should be used in total for the filter.
     * @param expectedNumberOElements defines the maximum number of elements the filter is expected to contain.
     */
    public TimingBloomFilter(int bitSetSize, int expectedNumberOfElements){
        this(bitSetSize/expectedNumberOfElements, 
                expectedNumberOfElements,
                (int) Math.round((bitSetSize/(double)expectedNumberOfElements)*Math.log(2.0)));
    }
    
    /**
     * Constructs an empty Bloom filter with a given false positive probability. The number of bits per
     * element and the number of hash functions is estimated
     * to match the false positive probability.
     *
     * @param falsePositiveProbability is the desired false positive probability.
     * @param expectedNumberOfElements is the expected number of elements in the Bloom filter.
     */
    public TimingBloomFilter(double falsePositiveProbability, int expectedNumberOfElements){
        this((int) (Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))) / Math.log(2)), // c = k / ln(2)
                expectedNumberOfElements,
                (int)Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2)))); // k = ceil(-log_2(false prob.))
    }

    /**
     * Construct a new Bloom filter based on existing Bloom filter data.
     *
     * @param bitSetSize defines how many bits should be used for the filter.
     * @param expectedNumberOfFilterElements defines the maximum number of elements the filter is expected to contain.
     * @param actualNumberOfFilterElements specifies how many elements have been inserted into the <code>filterData</code> BitSet.
     * @param filterData a BitSet representing an existing Bloom filter.
     */
    public TimingBloomFilter(int bitSetSize, int expectedNumberOfFilterElements, int actualNumberOfFilterElements, BitSet filterData)
    {
        this(bitSetSize, expectedNumberOfFilterElements);
        this.bitset = filterData;
        this.numberOfAddedElements = actualNumberOfFilterElements;
    }
    
    /**
     * Generates a digest based on the contents of a String.
     * @param val specifies the input data.
     * @param charset charset specifies the encoding of the input data.
     * @param timeCounter in TBF, special structure info
     * @return
     */
    public static int createHash(String val, Charset charset) {
        return createHash(val.getBytes(charset));
    }
    
    /**
     * Generates a digest based on the contents of a String.
     *
     * @param val specifies the input data. The encoding is expected to be UTF-8.
     * @return digest as long.
     */
    public static int createHash(String val) {
        return createHash(val, charset);
    }
    
    /**
     * Generates a digest based on the contents of an array of bytes.
     *
     * @param data specifies input data.
     * @return digest as long.
     */
    public static int createHash(byte[] data) {
        return createHashes(data, 1 )[0];
    }
    
    /**
     * Generates digests based on the contents of an array of bytes and splits the result into 4-byte int's and store them in an array. The
     * digest function is called until the required number of int's are produced. For each call to digest a salt
     * is prepended to the data. The salt is increased by 1 for each call.
     *
     * @param data specifies input data.
     * @param hashes number of hashes/int's to produce.
     * @return array of int-sized hashes
     */
    public static int[] createHashes(byte[] data, int hashes) {
        int[] result = new int[hashes];

        int k = 0;
        byte salt = 0;
        while (k < hashes) {
            byte[] digest;
            synchronized (digestFunction) {
                digestFunction.update(salt);
                salt++;
                digest = digestFunction.digest(data);                
            }
        
            for (int i = 0; i < digest.length/4 && k < hashes; i++) {
                int h = 0;
                for (int j = (i*4); j < (i*4)+4; j++) {
                    h <<= 8;
                    h |= ((int) digest[j]) & 0xFF;
                }
                result[k] = h;
                k++;
            }
        }
        return result;
    }
    
    /**
     * Compares the contents of two instances to see if they are equal.
     *
     * @param obj is the object to compare to.
     * @return True if the contents of the objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimingBloomFilter<E> other = (TimingBloomFilter<E>) obj;        
        if (this.expectedNumberOfFilterElements != other.expectedNumberOfFilterElements) {
            return false;
        }
        if (this.k != other.k) {
            return false;
        }
        if (this.bitSetSize != other.bitSetSize) {
            return false;
        }
        if (this.bitset != other.bitset && (this.bitset == null || !this.bitset.equals(other.bitset))) {
            return false;
        }
        return true;
    }

    /**
     * Calculates a hash code for this class.
     * @return hash code representing the contents of an instance of this class.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.bitset != null ? this.bitset.hashCode() : 0);
        hash = 61 * hash + this.expectedNumberOfFilterElements;
        hash = 61 * hash + this.bitSetSize;
        hash = 61 * hash + this.k;
        return hash;
    }


    /**
     * Calculates the expected probability of false positives based on
     * the number of expected filter elements and the size of the Bloom filter.
     * <br /><br />
     * The value returned by this method is the <i>expected</i> rate of false
     * positives, assuming the number of inserted elements equals the number of
     * expected elements. If the number of elements in the Bloom filter is less
     * than the expected value, the true probability of false positives will be lower.
     *
     * @return expected probability of false positives.
     */
    public double expectedFalsePositiveProbability() {
        return getFalsePositiveProbability(expectedNumberOfFilterElements);
    }

    /**
     * Calculate the probability of a false positive given the specified
     * number of inserted elements.
     *
     * @param numberOfElements number of inserted elements.
     * @return probability of a false positive.
     */
    public double getFalsePositiveProbability(double numberOfElements) {
        // (1 - e^(-k * n / m)) ^ k
        return Math.pow((1 - Math.exp(-k * (double) numberOfElements
                        / (double) bitSetSize)), k);

    }

    /**
     * Get the current probability of a false positive. The probability is calculated from
     * the size of the Bloom filter and the current number of elements added to it.
     *
     * @return probability of false positives.
     */
    public double getFalsePositiveProbability() {
        return getFalsePositiveProbability(numberOfAddedElements);
    }


    /**
     * Returns the value chosen for K.<br />
     * <br />
     * K is the optimal number of hash functions based on the size
     * of the Bloom filter and the expected number of inserted elements.
     *
     * @return optimal k.
     */
    public int getK() {
        return k;
    }

    /**
     * Sets all bits to false in the Bloom filter.
     */
    public void clear() {
        bitset.clear();
        numberOfAddedElements = 0;
    }

    /**
     * Adds an object to the Bloom filter. The output from the object's
     * toString() method is used as input to the hash functions.
     *
     * @param element is an element to register in the Bloom filter.
     */
    public void add(E element) {
       add(element.toString().getBytes(charset));
    }
    
    /**
     * 将一个新的元素加入Timing Bloom Filter中，需要使用对象的toString()方法输出作为输入
     * @param element
     * @param timing
     */
    public void add(E element, int timing){
        add(element.toString().getBytes(charset), timing);
    }

    /**
     * Adds an array of bytes to the Bloom filter.
     *
     * @param bytes array of bytes to add to the Bloom filter.
     */
    public void add(byte[] bytes) {
       int[] hashes = createHashes(bytes, k);
       for (int hash : hashes)
           bitset.set(Math.abs(hash % bitSetSize), true);
       numberOfAddedElements ++;
    }
    
    /**
     * 对应散列位置上，利用timing信息将其设置为true
     * 首先判断，timing信息是否在1-N范围之内；然后将其转为二进制，利用offset进行设置
     * 
     * @param bytes  转化为字节序列的元素，将其加入Bloom Filter
     * @param timing 对应的wrappcounter的时间戳信息
     */
    public void add(byte[] bytes, int timing){
        int[] hashes =createHashes(bytes,k);

        char[] binaryTrue=getEntryTrueBit(timing);
        if(binaryTrue.length!=BloomFilterCfg.getEntryBitSize()){
            System.out.println("Warnning: BinaryChar is not corrected");
            return;
        }
        for(int hash : hashes){
            int offset=Math.abs(hash%entrySize);
            int index=-1;
            for(char i :binaryTrue){
                index++;
                if(i=='0') {
                    bitset.set(offset*BloomFilterCfg.getEntryBitSize()+index,false);
                }
                else{
                    bitset.set(offset*BloomFilterCfg.getEntryBitSize()+index,true);  //通过offset计算出timing对应应该设置的区间
                }
            }
        }
        numberOfAddedElements ++;
    }
    
    /**
     * 返回时间戳信息对应的二进制为1的位
     * @param timing
     * @return
     */
    public char[] getEntryTrueBit(int timing){
        if(timing<=0||timing >ClickStreamCfg.WINDOW_SIZE){
            System.out.println("Warnning: Timing information overflow! excepted: 1~"+ClickStreamCfg.WINDOW_SIZE +" actual:"+timing);
            timing=0;
        }
      //  int[] binaryTrue=new int[BloomFilterCfg.getEntryBitSize()];
        String binaryChar=Integer.toBinaryString(timing);
        int bits=BloomFilterCfg.getEntryBitSize()-binaryChar.length();
        for(int b=0; b<bits && bits>0;b++){
            binaryChar="0"+binaryChar;
        }
        return binaryChar.toCharArray();
        
    }
    
    /**
     * 返回时间戳信息对应的二进制为1的位
     * @param timing
     * @return
     */
    public String getEntryBit(int timing){
        if(timing<=0||timing >ClickStreamCfg.WINDOW_SIZE){
            System.out.println("Warnning: Timing information overflow! excepted: 1~"+ClickStreamCfg.WINDOW_SIZE +" actual:"+timing);
            timing=0;
        }
      //  int[] binaryTrue=new int[BloomFilterCfg.getEntryBitSize()];
        String binaryChar=Integer.toBinaryString(timing);
        int bits=BloomFilterCfg.getEntryBitSize()-binaryChar.length();
        for(int b=0; b<bits && bits>0;b++){
            binaryChar="0"+binaryChar;
        }
        return binaryChar;
        
    }

    /**
     * Adds all elements from a Collection to the Bloom filter.
     * @param c Collection of elements.
     */
    public void addAll(Collection<? extends E> c) {
        for (E element : c)
            add(element);
    }
        
    /**
     * Returns true if the element could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param element element to check.
     * @return true if the element could have been inserted into the Bloom filter.
     */
    public boolean contains(E element) {
        return contains(element.toString().getBytes(charset));
    }
    
    /**
     * 判断元素是否存在，并判断active(true) or expired(false)
     * @param element
     * @param timing
     * @return
     */
    public boolean containsAndPresents(E element,int timing){
        return containsAndPresents(element.toString().getBytes(charset), timing);
        
    }

    /**
     * Returns true if the array of bytes could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param bytes array of bytes to check.
     * @return true if the array could have been inserted into the Bloom filter.
     */
    public boolean contains(byte[] bytes) {
        int[] hashes = createHashes(bytes, k);
        BitSet tempbitset=new BitSet(BloomFilterCfg.getEntryBitSize());
        int count=0;
        for (int hash : hashes) {
            int offset=Math.abs(hash%entrySize)*BloomFilterCfg.getEntryBitSize();
            for(int i=0; i<BloomFilterCfg.getEntryBitSize();i++){
                if(count==0){
                    tempbitset.set(i, bitset.get(offset+i));
                }
                else{
                    if(bitset.get(offset+i)!=tempbitset.get(i)) return false;
                }
                
                count++;
            }
        }
        return true;
    }
    
//    public boolean contains(byte[] bytes) {
//        int[] hashes = createHashes(bytes, k);
//        boolean flag=true;
//        for (int hash : hashes) {
//            if(flag==false){
//                return false;
//            }
//            int offset=Math.abs(hash%entrySize)*BloomFilterCfg.getEntryBitSize();
//            for(int i=0; i<BloomFilterCfg.getEntryBitSize();i++){
//                if(bitset.get(offset+i)){
//                    flag=true;
//                    break;
//                }
//                flag=false;
//            }
//        }
//
//        return flag;
//    }
//    
    
    /**
     * 判断是否存在，并且判断是否是要删除的数据
     * @param bytes
     * @param timing
     * @return
     */
    public boolean containsAndPresents(byte[] bytes, int timing){
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
            boolean flag=false;
            int offset=Math.abs(hash%entrySize)*BloomFilterCfg.getEntryBitSize();
            String timeCounter="";
            for(int i=0; i<BloomFilterCfg.getEntryBitSize();i++){
                if(bitset.get(offset+i)){
                    flag=true;
                    timeCounter=timeCounter+"1";  //累计保存的时间戳
                }
                else{
                    timeCounter=timeCounter+"0";
                }
            }
            //计算得到最近一次的时间戳
            int timestampe=Integer.valueOf(timeCounter);
            if (flag==false || timestampe!=timing) {
                return false;
            }
        }
        return true;
    }
    
    public boolean containsAndPresents(byte[] bytes, int timing,int C){

        //TODO 当时间戳窗口扩大C，加快删除的效率
        return true;
    }

    /**
     * Returns true if all the elements of a Collection could have been inserted
     * into the Bloom filter. Use getFalsePositiveProbability() to calculate the
     * probability of this being correct.
     * @param c elements to check.
     * @return true if all the elements in c could have been inserted into the Bloom filter.
     */
    public boolean containsAll(Collection<? extends E> c) {
        for (E element : c)
            if (!contains(element))
                return false;
        return true;
    }
    
    /**
     * 删除过期的timing信息
     *  
     * @param timing
     */
    public void delete(int timing){
        String timestamp=getEntryBit(timing);
        int entryBits=BloomFilterCfg.getEntryBitSize();
        
        int entries=0;
       // int count=0;
        String compare="";
        for(int pos=0; pos<bitset.length();pos++){
            compare=compare+((bitset.get(pos))==false?0:1);
            if((pos+1)%BloomFilterCfg.getEntryBitSize()==0){
                if(compare.equals(timestamp)){
                    for(int c=0; c<entryBits; c++){
                        bitset.set(pos-c, false);
                    }
                }
                entries=entries+1;
                compare="";
            }
        }
        
        //System.out.println("Delete item");
//        for(int pos=0;pos<bitset.length();pos++){
//            Boolean flag= 
//                    ((timestamp[pos%BloomFilterCfg.getEntryBitSize()]=='0')? false :true); 
//            if(flag!=bitset.get(pos)){
//                entries=entries+1;
//                pos=entries*BloomFilterCfg.getEntryBitSize()-1;
//                continue;
//            }
//            else{
//                count++; 
//                
//                //如果匹配的
//                if(count==BloomFilterCfg.getEntryBitSize()){
//                    for(int j=0; j<count ;j++){
//                        bitset.set(entries*BloomFilterCfg.getEntryBitSize()+j, false);
//                    }
//                    
//                    count=0;
//                    entries=entries+1;
//                }
//                
//            }
//        }
    }

    /**
     * Read a single bit from the Bloom filter.
     * @param bit the bit to read.
     * @return true if the bit is set, false if it is not.
     */
    public boolean getBit(int bit) {
        return bitset.get(bit);
    }
    

    /**
     * Set a single bit in the Bloom filter.
     * @param bit is the bit to set.
     * @param value If true, the bit is set. If false, the bit is cleared.
     */
    public void setBit(int bit, boolean value) {
        bitset.set(bit, value);
    }

    /**
     * Return the bit set used to store the Bloom filter.
     * @return bit set representing the Bloom filter.
     */
    public BitSet getBitSet() {
        return bitset;
    }

    /**
     * Returns the number of bits in the Bloom filter. Use count() to retrieve
     * the number of inserted elements.
     *
     * @return the size of the bitset used by the Bloom filter.
     */
    public int size() {
        return this.bitSetSize;
    }

    /**
     * Returns the number of elements added to the Bloom filter after it
     * was constructed or after clear() was called.
     *
     * @return number of elements added to the Bloom filter.
     */
    public int count() {
        return this.numberOfAddedElements;
    }

    /**
     * Returns the expected number of elements to be inserted into the filter.
     * This value is the same value as the one passed to the constructor.
     *
     * @return expected number of elements.
     */
    public int getExpectedNumberOfElements() {
        return expectedNumberOfFilterElements;
    }

    /**
     * 取得entrySize *
     * @return 返回 entrySize。
     */
    public int getEntrySize()
    {
        return entrySize;
    }

    /**
     * 设置entrySize
     * @param entrySize 要设置的 entrySize。
     */
    public void setEntrySize(int entrySize)
    {
        this.entrySize = entrySize;
    }

}
