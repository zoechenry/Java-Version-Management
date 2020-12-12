import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    /*
    该类的对象将作为key_value对象的元素，用于计算value对应的key
    进行了方法的重写，会根据多态，自动选择合适的方法计算key值
     */

    private String hashValue;

    public Hash(){}

    public String getHashValue() {
        return hashValue;//获取hash值
    }

    //计算Blob的hash
    public void setHashValue(File inputFile) throws Exception{
        FileInputStream is = new FileInputStream(inputFile);
        MessageDigest complete = MessageDigest.getInstance("SHA-1");//创建MessageDigest用于计算hash值
        byte[] buffer = new byte[1024];//创建一个缓冲区计算hash值
        int numRead=0;
        while (numRead!=-1){
            numRead = is.read(buffer);//缓冲区读入numRead数量的字符
            if (numRead>0)
                complete.update(buffer,0,numRead);
        }
        is.close();
        setHashValue(complete);
    }

    //从StringBuilder中计算hash值
    public void setHashValue(StringBuilder value) throws NoSuchAlgorithmException {
        byte[] buffer = value.toString().getBytes();
        MessageDigest complete = MessageDigest.getInstance("SHA-1");//创建MessageDigest用于计算hash值
        complete.update(buffer,0, buffer.length);
        setHashValue(complete);
    }

    //将消息队列中的hash值取出
    private void setHashValue(MessageDigest complete){
        StringBuilder result = new StringBuilder();
        for (byte b : complete.digest()) {
            String temp = Integer.toString(b & 0xFF, 16);
            if(temp.length()==1)
                result.append('0');
            result.append(temp);
        }
        this.hashValue = result.toString();
    }
}