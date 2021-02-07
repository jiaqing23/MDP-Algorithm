package simulation;

public class MDFString {
   String mdfHex="";
   String mdfBinary="";

   public String getMDFHex(){
       return mdfHex;
   }

   public void setMDFHex(String input){
       mdfHex = input;
   }

    public String getMDFBinary(){
        return mdfBinary;
    }

    public void setMDFBinary(String input){
        mdfBinary = input;
    }

    private void hexToBin(){
        int i = Integer.parseInt(mdfHex, 16);
        String bin = Integer.toBinaryString(i);
        mdfBinary = bin;
    }

    private void binToHex(){
        int decimal = Integer.parseInt(mdfBinary,2);
        String hexStr = Integer.toString(decimal,16);
        mdfHex = hexStr;
    }
}
