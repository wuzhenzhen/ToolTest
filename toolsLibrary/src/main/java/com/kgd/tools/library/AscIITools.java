package com.kgd.tools.library;

import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by wzz on 2017/03/01.
 * wuzhenzhen@tiamaes.com
 * ������ع�����
 *
 *  String intToHexString(int algorism)  ʮ����ת��Ϊʮ�������ַ���
 *  String encode(String str)  ���ַ��������16��������,�����������ַ����������ģ�
 *  String decode(String bytes) ��16�������ֽ�����ַ���,�����������ַ����������ģ�
 *  intToHexString(int algorism, int length)    ʮ����ת��Ϊʮ�������ַ���(����length,ǰ�油0)
 *
 *
 *  getEscape(String content)       ��������ת�壺 ����ʱ��7D��7Eת��
 *  getUnEscape(String content)     �������� ת�� 7D02->7E,   7D01->7D
 */

public class AscIITools {

    /**
     * ʮ����ת��Ϊʮ�������ַ���
     *
     * @param algorism
     *            int ʮ���Ƶ�����
     * @return String ��Ӧ��ʮ�������ַ���
     */
    public static String intToHexString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    /**
     *  long ת��Ϊʮ�������ַ���
     * @param lng
     * @return
     */
    public static String longToHexString(long lng){
        String result = "";
        result = Long.toHexString(lng);

        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    /**
     *  long ת��Ϊָ������ʮ�������ַ���������ǰ�油0
     * @param lng
     * @param length
     * @return
     */
    public static String longToHexString(long lng, int length){
        String result = "";
        result = Long.toHexString(lng);

        int resultLength = result.length();
        if(resultLength < length){
            for(int i=0; i<length-resultLength; i++){
                result="0"+result;
            }
        }else{
            result = result.substring(result.length()-length,result.length());
        }
        return result.toUpperCase();
    }
    /**
     * ʮ����ת��Ϊʮ�������ַ���(����length,ǰ�油0)
     * �� AscIITools.intToHexString(1,4)  = 0001
     * @param algorism
     * @param length    ת��Ϊʮ�����Ƶĳ���
     * @return
     */
    public static String intToHexString(int algorism, int length){
        String result = "";
        result = Integer.toHexString(algorism);

        int resultLength = result.length();
        if(resultLength < length){
            for(int i=0; i<length-resultLength; i++){
                result="0"+result;
            }
        }else{
            result = result.substring(result.length()-length,result.length());
        }
        return result.toUpperCase();
    }

    /*
     * 16���������ַ���
     */
    private static String hexString = "0123456789ABCDEF";

    /*
     * ���ַ��������16��������,�����������ַ����������ģ�
     */
    public static String encode(String str) {
        // ����Ĭ�ϱ����ȡ�ֽ�����
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // ���ֽ�������ÿ���ֽڲ���2λ16��������
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /*
     * ��16�������ֽ�����ַ���,�����������ַ����������ģ�
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // ��ÿ2λ16����������װ��һ���ֽ�
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    // �ж�������ż����λ���㣬���һλ��1��Ϊ������Ϊ0��ż��
    static public int isOdd(int num)
    {
        return num & 0x1;
    }

    //----------Hex�ַ���תint------------------
    static public int HexToInt(String inHex)
    {
        return Integer.parseInt(inHex, 16);
    }

    //----------Hex�ַ���תlong-----------------
    static public long HexToLong(String hexStr){
        return Long.parseLong(hexStr, 16);
    }

    //----------Hex�ַ���תbyte-----------------
    static public byte HexToByte(String inHex)
    {
        return (byte) Integer.parseInt(inHex,16);
    }

    //----------1�ֽ�ת2��Hex�ַ�----------------
    static public String Byte2Hex(Byte inByte){
        return String.format("%02x", inByte).toUpperCase();
    }
    //----------�ֽ�����תתhex�ַ���--------------------
    static public String ByteArrToHex(byte[] inBytArr){
        StringBuilder strBuilder=new StringBuilder();
        int j=inBytArr.length;
        for (int i = 0; i < j; i++)
        {
            strBuilder.append(Byte2Hex(inBytArr[i]));
//            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }
    //-------------�ֽ�����תתhex�ַ�������ѡ����----------------------
    static public String ByteArrToHex(byte[] inBytArr, int offset, int byteCount){
        StringBuilder strBuilder=new StringBuilder();
        int j=byteCount;
        for (int i = offset; i < j; i++)
        {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }
    //--------------hex�ַ���ת�ֽ�����------------------------------
    static public byte[] HexToByteArr(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen)==1)
        {//����
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {//ż��
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2)
        {
            result[j]=HexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }
    //-------------�ֽ�����תhex�ַ����� ����length ���ȣ�ǰ�油0
    public static String ByteArrToHex(byte[] inBytArr, int length){
        String result = "";
        result = ByteArrToHex(inBytArr);

        int resultLength = result.length();
        if(resultLength < length){
            for(int i=0; i<length-resultLength; i++){
                result="0"+result;
            }
        }else{
            result = result.substring(result.length()-length,result.length());
        }
        return result.toUpperCase();
    }

    /**
     * �������ַ���תʮ�������ַ���  ��"00000011"->"03"
     * @param bString
     * @return
     */
    public static String binaryStr2hexStr(String bString){
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4)
        {
            iTmp = 0;
            for (int j = 0; j < 4; j++)
            {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     *  ʮ�������ַ���ת�������ַ���  ��03--��00000011
     * @param hexString
     * @return
     */
    public static String hexStr2binaryStr(String hexString){
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++)
        {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * bytesת����ʮ������������
     *
     * @param src
     * @return
     */
    public static String[] bytes2HexStrings(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String[] str = new String[src.length];

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0XFF;
            String hv = Integer.toHexString(v);
            if (hv.length() == 1) {
                str[i] = "0" + hv;
            } else {
                str[i] = hv;
            }
        }
        return str;
    }


    /**
     * ��byte����ת����16�����ַ���
     * @param buf
     * @return
     * */
    public static String Bytes2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**��16�����ַ���ת��Ϊbyte����          ͬHexToByteArr
     * @param hexStr
     * @return
     * */
    public static byte[] HexStr2Bytes(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * @����: BCD��תΪ10���ƴ�(����������)
     * @����: BCD��
     * @���: 10���ƴ�
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * @����: 10���ƴ�תΪBCD��
     * @����: 10���ƴ�
     * @���: BCD��
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * @����: ���16���ƴ��Ƿ�Ϸ�
     * @����: 16���ƴ�
     * @���: true/false
     */
    public static boolean isHex(String hexStr){
        for(int i = 0; i < hexStr.length(); i++) {// �ж��Ƿ�����16������
            if (!((hexStr.charAt(i) <= '9' && hexStr.charAt(i) >= '0')
                    ||(hexStr.charAt(i) <= 'F' && hexStr.charAt(i) >= 'A')
                    || (hexStr.charAt(i) <= 'f' && hexStr.charAt(i) >= 'a'))) {
                System.out.println("hex error");
                return false;
            }
        }
        return true;
    }

    /**
     * @����: format longitude and latitude
     * 	��39936472/116267048 ת��Ϊdouble����39.936472/116.267048
     * @����: int
     * @���:
     */
    public static double formatLAL(int lal){
        int remainder = lal%1000000;
        int result = lal/1000000;
        StringBuffer sb = new StringBuffer();
        sb.append(result);
        sb.append(".");
        sb.append(remainder);
        return Double.parseDouble(sb.toString());
    }

    /**
     * @����: 16�����ַ�����γ��תdouble ����
     * 	��39936472/116267048 ת��Ϊdouble����39.936472/116.267048
     * @����: String
     * @���:
     * LAL = Latitude and Longtitude
     */
    public static double HexToLAL(String hexStr){
        return formatLAL(HexToInt(hexStr));
    }


    //------------byte����---����У��---���ͼ���---------------------------
    /**
     * @����: byte����---����У��
     * 	 �磺byte datas[] = {0x00,0x01,0x11,0x00}; byte checkCode = 0x10;
     *   0x00^0x01^0x11^0x00 = 0x10
     * @����: byte ���飬 byte������
     * @���:
     */
    public static boolean xorVerify(byte datas[], byte checkCode){
        byte result = datas[0];
        for (int i = 1; i < datas.length; i++){
            result ^= datas[i];
        }
        if(result == checkCode)
            return true;
        return false;
    }

    //------------ʮ�������ַ���---���ͼ���---------------------------
    /**
     * @category  ʮ�������ַ���---���ͼ���
     *  �磺String str = "0001100102010110A3B8A3B8A1FAB8DFC1D6BED3D7A1C7F8"; byte checkCode = 0x0D;
     * @param str  ʮ�������ַ���
     * @param checkCode  У����
     * @return У����
     */
    public static boolean xorVerify(String str, byte checkCode){
        byte result = HexToByte(str.substring(0,2));
        for (int i=2; i<str.length(); i+=2){
            String b = str.substring(i, i+2);
            result ^= HexToByte(b);
        }
        if(result == checkCode)
            return true;
        return false;
    }

    /**
     *  ��������
     * @param str
     * @return
     */
    public static byte getXor(String str){
        if(str == null || str.equals("")) return 0;
        byte result = HexToByte(str.substring(0,2));
        for (int i=2; i<str.length(); i+=2){
            String b = str.substring(i, i+2);
            result ^= HexToByte(b);
        }
        return result;
    }

    /**
     * ��������
     * @param b
     * @return
     */
    public static byte getXor(byte[] b){
        byte result = b[0];
        for (int i=1; i<b.length; i+=1){
            result ^= b[i];
        }
        return result;
    }

    /**
     *  �õ���У����
     * @param hexString     ʮ�������ַ���
     * @param length    У���볤��  length �ֽ�
     * @return
     */
    public static String getSumCheck(String hexString, int length){
        if(hexString == null || hexString.equals("")) return "";
        long sum = HexToInt(hexString.substring(0,2));
        for (int i=2; i<hexString.length(); i+=2){
            String b = hexString.substring(i, i+2);
            sum += HexToInt(b);
        }
        String result = longToHexString(sum);
        if(result.length() > length*2){
            return result.substring(result.length()-length*2);
        }else{
            String temp = result;
            for(int i=0; i<length*2 - result.length(); i++){
                temp = "0"+temp;
            }
            return temp;
        }
    }

    /**
     *  ��У��
     * @param hexString ʮ�������ַ���
     * @param checkCode ʮ������У����
     * @return
     */
    public static boolean SumCheck(String hexString, String checkCode){
        String nCheckCode = getSumCheck(hexString, checkCode.length()/2);
        if(nCheckCode.equals(checkCode)){
            return true;
        }
        return false;
    }

    /**
     * У���
     *
     * @param msg ��Ҫ����У��͵�byte����
     * @param length У���λ��
     * @return �������У�������
     */
    public static byte[] SumCheck(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** ��Byte���λ���� */
        for (byte byteMsg : msg) {
            long mNum = ((long)byteMsg >= 0) ? (long)byteMsg : ((long)byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** λ����ת��ΪByte���� */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte)(mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return mByte;
    }



    /**
     *  ��������ת�壺 ����ʱ��7D��7Eת��
     *  0x7e <��������> 0x7d �����һ�� 0x02��
     *  0x7d <��������> 0x7d �����һ�� 0x01��
     * @param content
     * @return
     */
    public static String getEscape(String content){
        if(content.length()%2 != 0){
            System.out.println("getEscape content is has error"+content);
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i<content.length(); ){
            String str = content.substring(i,i+2);
            if(str.equals("7D")){
                sb.append("7D01");
            }else if (str.equals("7E")){
                sb.append("7D02");
            }else{
                sb.append(str);
            }
            i+=2;
        }
//        System.out.println(content+"\n"+sb.toString());
        return sb.toString();
    }

    /**
     *  �������� ת��
     * @param content
     * @return
     */
    public static String getUnEscape(String content){
        if(content.length()%2 != 0){
            System.out.println("getEscape content is has error"+content);
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i<content.length()-2; ){
            String str = content.substring(i,i+4);
            if(str.equals("7D02")){
                sb.append("7E");
                i+=4;
            }else if (str.equals("7D01")){
                sb.append("7D");
                i+=4;
            }else{
                sb.append(content.substring(i,i+2));
                i+=2;
            }
            //����β
            if(i==content.length()-2){
                    sb.append(content.substring(i,i+2));//���β
            }
        }
//        System.out.println(content+"\n"+sb.toString());
        return sb.toString();
    }

    /**
     *  �õ�ָ�����ȵ�byte ���飨���ں�0��
     *  �������length=10 ,"TM-01".getBytes()=bytes= [84,77,45,48,49] ����Ϊ[84,77,45,48,49,0,0,0,0,0]
     *  ʮ������Ϊ��54 4D 2D 30 31 00 00 00 00 00
     *  ��������bytes ���length-bytes.length ��0
     * @param bytes
     * @param length
     * @return   ����ָ����bytes ���ݳ���
     */
    public static byte[] getLengthBytes(byte [] bytes, int length){
        if(bytes.length>length){
            Log.e("AscIITools","getLengthBytes is error = bytes.length>length");
        }
        byte [] result = new byte [length];
        for(int i=0; i<length; i++){
            if(i<bytes.length){
                result[i]=bytes[i];
            }else{
                result[i]=0;
            }
        }
        return result;
    }

    /**
     *  �õ�ָ�����ȵ�BCD, ����ʱǰ��0
     * @param bcd   ԭʼbcd
     * @param length  ����
     * @return   �� bcd= "123456", length = 10  ��result = "0000123456"
     */
    public static String getLengthBCD(String bcd, int length){
        if(bcd.length()>length){
            Log.e("AscIITools","getLengthBCD is error = bcd.length>length");
        }
        String result = bcd;
        for(int i =0; i<length-bcd.length(); i++){
            result = "0"+result;
        }
        return result;
    }

}
