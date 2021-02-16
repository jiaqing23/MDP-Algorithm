package map;

import java.util.Locale;

public class MDFString {
    String mdfHex="";
    String mdfBinary="";

    public String getMDFHex(){
       return mdfHex;
   }

    public void setMDFHex(String input){
        mdfHex = input;
        System.out.println(mdfHex);
        hexToBin();
   }

    public String getMDFBinary(){
        return mdfBinary;
    }

    public void setMDFBinary(String input){
        mdfBinary = input;
        binToHex();
    }

    private void hexToBin(){
        String bin="";
        int j=0;
        for(int i=0;i<76;i++){
            j = Integer.parseInt(mdfHex.substring(i,i+1), 16);
            if(j<8){
                for(int k=0; k<(4-Integer.toBinaryString(j).length()); k++){
                    bin+=0;
                }
                bin+=Integer.toBinaryString(j);
            }else{
                bin += Integer.toBinaryString(j);
            }
        }
        mdfBinary = bin;
    }

    private void binToHex(){
        String hexStr="";
        int decimal=0;
        for(int i=0;i<304;i+=4){
            decimal = Integer.parseInt(mdfBinary.substring(i,i+4),2);
            hexStr += Integer.toString(decimal, 16).toUpperCase(Locale.ROOT);
        }
        mdfHex = hexStr;
    }

}
