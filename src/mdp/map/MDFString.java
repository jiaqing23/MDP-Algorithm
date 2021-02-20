package mdp.map;

import java.util.Locale;

public class MDFString {
    String mdfHex="";
    String mdfBinary="";
    String mdfHex2="";
    String mdfBinary2="";

    public String getMDFHex(){
       return mdfHex;
   }

    public void setMDFHex(String input){
        mdfHex = input;
        hexToBin(mdfHex, 1);
   }

    public String getMDFBinary(){
        return mdfBinary;
    }

    public void setMDFBinary(String input){
        mdfBinary = input;
        binToHex(mdfBinary, 1);
    }

    public String getMDFHex2(){
        return mdfHex2;
    }

    public void setMDFHex2(String input){
        mdfHex2 = input;
        hexToBin(mdfHex2, 2);
    }

    public String getMDFBinary2(){
        return mdfBinary2;
    }

    public void setMDFBinary2(String input){
        mdfBinary2 = input;
        binToHex(mdfBinary2, 2);
    }

    private void hexToBin(String mdfH, int num){
        String bin="";
        int j=0;
        for(int i=0;i<76;i++){
            j = Integer.parseInt(mdfH.substring(i,i+1), 16);
            if(j<8){
                for(int k=0; k<(4-Integer.toBinaryString(j).length()); k++){
                    bin+=0;
                }
                bin+=Integer.toBinaryString(j);
            }else{
                bin += Integer.toBinaryString(j);
            }
        }
        if(num==1){
            mdfBinary = bin;
        }else{
            mdfBinary2 = bin;
        }

    }

    private void binToHex(String mdfB, int num){
        String hexStr="";
        int decimal=0;
        for(int i=0;i< mdfB.length();i+=4){
            decimal = Integer.parseInt(mdfB.substring(i,i+4),2);
            hexStr += Integer.toString(decimal, 16).toUpperCase(Locale.ROOT);
        }

        if (num==1){
            mdfHex = hexStr;
        }else{
            mdfHex2=hexStr;
        }
    }

}
