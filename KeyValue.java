import java.io.*;
import java.util.HashMap;

public abstract class KeyValue {

    /*
    虚类，供包括Blob、Tree、Commit在内的其他类继承，包括了一些通用的变量和方法。
    变量包括：进行操作的文件对象、key-value存储路径以及计算key值的Hash对象。
    方法包括：从Hash对象中取出key、检查某个key是否以及存在key-value、根据key查找value并返回
     */

    protected File inputFile;
    protected String storagePath;
    protected Hash key;

    public KeyValue(File inputFile, String storagePath){
        this.inputFile = inputFile;
        this.storagePath = storagePath;
        this.key = new Hash();
    }

    public String getKey(){
        return key.getHashValue();
    }

    //通过StringBuilder创建模块的Key-Value文件
    protected void createKeyValue(StringBuilder value) throws Exception{
        key.setHashValue(value);//计算hash值
        File keyValueFile = new File(storagePath, getKey());//创建Tree文件
        BufferedWriter bw = new BufferedWriter(new FileWriter(keyValueFile));
        bw.write(value.toString());
        bw.flush();
        bw.close();
    }

    //创建Blob模块的Key-Value文件
    protected void createKeyValue() throws Exception {
        key.setHashValue(inputFile);//计算key值
        if(isValuableKey())//若Blob文件已经存在，直接返回
            return;
        File keyValueFile = new File(storagePath, getKey());//若还没有对应的Blob文件，新建一个
        //使用缓冲区写入Blob文件
        FileInputStream is = new FileInputStream(inputFile);
        FileOutputStream os = new FileOutputStream(keyValueFile);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while (numRead != -1) {
            numRead = is.read(buffer);
            //缓冲区读入numRead数量的字符
            if (numRead > 0)
                os.write(buffer, 0, numRead);
        }
        is.close();
        os.close();
    }

    //判断给定的key是否对应一个有效的key-value文件
    protected boolean isValuableKey(){
        String hash = getKey();
        File[] files = new File(storagePath).listFiles();//列出全部的key-value文件
        if(files!=null) {
            for (File f : files) {
                if (f.getName().equals(hash))
                    return true;
            }
        }
        return false;
    }

    //给定key，查找对应的文件并返回（可调用）
    public File getValue(){
        String hash = getKey();
        File[] files = new File(storagePath).listFiles();//列出全部的key-value文件
        if(files!=null) {
            for (File f : files)
                if (f.getName().equals(hash))//若找到key对应的文件，则返回这个文件
                    return f;
        }
        //没找到对应的文件，返回一个临时的空文件
        return (new File(storagePath,"temp"));
    }

    //从Head文件中获取最新一次commit的key值
    protected String getLatestCommit() throws IOException {
        File HEADFile = new File(storagePath + "\\HEAD");

        if(!HEADFile.exists()){
            return null;
        }
        else{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
            BufferedReader br = new BufferedReader(isr);
            File branchFile = new File(storagePath + "\\branch\\" + br.readLine());
            isr = new InputStreamReader(new FileInputStream(branchFile));
            br = new BufferedReader(isr);
            return br.readLine();
        }
    }

    //获取commit的信息
    protected String getInfoOfHomeFolder(String latestCommit, String info) throws IOException {
        if (latestCommit == null) {
            return null;//不存在前一次commit则latestHashOfHomeFolder为null
        } else {
            File latestCommitFile = new File(storagePath + "\\" + latestCommit);//打开上一次的commit文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(latestCommitFile));
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            HashMap<String, String> infoSave = new HashMap<String, String>();
            while(line != null){
                String[] hashSplit = line.split(": ");//读取latestHashOfHomeFolder
                infoSave.put(hashSplit[0],hashSplit[hashSplit.length - 1]);
                line = br.readLine();
            }
            return (String) infoSave.get(info);
        }
    }

    // 更新（创建）branch文件，存储这次commit的Key
    protected void createBranch(String str) throws IOException {
        String branchPath =  storagePath + "\\branch";
        File branch = new File(branchPath);
        if  (!branch.exists())
            branch.mkdir();

        BufferedWriter headOut = new BufferedWriter(new FileWriter(storagePath + "\\branch\\" + str));
        if (str.equals("Main"))
            headOut.write(getKey());
        else{
            headOut.write(getLatestCommit());
        }
        System.out.println("创建分支"+ str +"成功！");
        headOut.flush();
        headOut.close();
    }

    protected void changeBranch() throws IOException {
        File HEADFile = new File(storagePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        BufferedWriter branchOut = new BufferedWriter(new FileWriter(storagePath + "\\branch\\" + str));
        branchOut.write(getKey());
        branchOut.flush();
        branchOut.close();
    }

    protected void Branch() throws IOException {
        File[] files = new File(storagePath + "\\branch").listFiles();
        System.out.println("目前已有分支：");
        for (File f : files)
            System.out.println(f.getName());
        File HEADFile = new File(storagePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        System.out.println("当前所在分支：" + str);
    }

    /**
     * 更新（创建）HEAD文件，存储当前branch
     * @param str
     * @throws IOException
     */
    protected void createHead(String str) throws IOException {
        File head = new File(storagePath + "\\HEAD");
        BufferedWriter headOut = new BufferedWriter(new FileWriter(storagePath + "\\HEAD"));
        headOut.write(str);
        headOut.flush();
        headOut.close();
    }

    public boolean checkoutBranch(String branchName) throws IOException {
        File[] files = new File(storagePath + "\\branch").listFiles();
        for (File f : files)
            if (f.getName().equals(branchName)){
                createHead(branchName);
                return true;
            }
        return false;
    }
}
