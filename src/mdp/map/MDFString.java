package mdp.map;

import java.util.Locale;

public class MDFString {
    String mdfHex1="";
    String mdfBinary1="";
    String mdfHex2="";
    String mdfBinary2="";

    public void setMDFHex1(String input){
        mdfHex1 = input;
        hexToBin(mdfHex1, 1);
   }

    public void setMDFHex2(String input){
        mdfHex2 = input;
        hexToBin(mdfHex2, 2);
    }

    public void setMDFBinary1(String input){
        mdfBinary1 = input;
        binToHex(mdfBinary1, 1);
    }

    public void setMDFBinary2(String input){
        mdfBinary2 = input;
        binToHex(mdfBinary2, 2);
    }

    public String getMDFHex1(){
        return mdfHex1;
    }

    public String getMDFHex2(){
        return mdfHex2;
    }

    public String getMDFBinary1(){
        return mdfBinary1;
    }

    public String getMDFBinary2(){
        return mdfBinary2;
    }

    private void hexToBin(String mdfH, int num){
        String bin="";
        for(int i = 0; i < mdfH.length(); i++){
            int j = Integer.parseInt(mdfH.substring(i,i+1), 16);
            if(j < 8){
                for(int k = 0; k < (4-Integer.toBinaryString(j).length()); k++){
                    bin += 0;
                }
                bin += Integer.toBinaryString(j);
            }else{
                bin += Integer.toBinaryString(j);
            }
        }
        if(num==1){
            mdfBinary1 = bin;
        }
        else{
            mdfBinary2 = bin;
        }
    }

    private void binToHex(String mdfB, int num){
        String hexStr="";
        for(int i = 0; i < mdfB.length(); i+=4){
            int decimal = Integer.parseInt(mdfB.substring(i,i+4),2);
            hexStr += Integer.toString(decimal, 16).toUpperCase(Locale.ROOT);
        }
        if (num==1){
            mdfHex1 = hexStr;
        }
        else{
            mdfHex2 = hexStr;
        }
    }

}
