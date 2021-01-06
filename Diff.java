import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Diff {

    /**
     * Compare a file before and after a modification, print its difference.
     *
     * @param before, after
     * @return
     */
    public static void compareFile(File before, File after) throws IOException {
        Scanner input = new Scanner(before);
        System.out.println("Below is the diff file of a/"+before.getPath()+" and b/"+ after.getPath());
        ArrayList<String> befStr = new ArrayList<>();
        ArrayList<String> aftStr = new ArrayList<>();
        while(input.hasNextLine()){
            befStr.add(input.nextLine());
        }
        input = new Scanner(after);
        while (input.hasNextLine()){
            aftStr.add(input.nextLine());
        }
        String difference = compareFile(befStr, aftStr);
        System.out.println(difference);
    }

    /**
     * Compare a file before and after a modification, return its changes.
     *
     * @param befStr, aftStr
     * @return difference
     */
    public static String compareFile(ArrayList<String> befStr, ArrayList<String> aftStr) {
        int len1 = befStr.size();
        int len2 = aftStr.size(); //m，n代表变化后和变化前的总长度
        int[][] dp = new int[len1+1][len2+1];
        String[][] dpString = new String[len1+1][len2+1];

        // Myers算法
        dpString[0][0] = "";

        for (int i=0; i<len1; i++) {
            dp[i+1][0] = i;
            dpString[i+1][0] = "- " + befStr.get(i) + "\n";
        }
        for (int j=0; j<len2; j++) {
            dp[0][j+1] = j;
            dpString[0][j+1] = "+ " + aftStr.get(j) + "\n";
        }
        for (int i=1; i<=len1; i++) {
            for (int j=1; j<=len2; j++) {
                dpString[i][j] = dpString[i-1][j] + dpString[i][j-1];
                int minRes = 1 + dp[i-1][j-1];
                if (befStr.get(i-1).equals(aftStr.get(j-1)) ) {
                    dp[i][j] = dp[i - 1][j - 1];
                    dpString[i][j] = dpString[i-1][j-1];
                    minRes = dp[i-1][j-1];
                }
                if (minRes > dp[i-1][j] + 1) {
                    minRes = dp[i-1][j] + 1;
                    dpString[i][j] = dpString[i-1][j] + "- " + befStr.get(i-1) + "\n";
                }
                if (minRes > dp[i][j-1] + 1) {
                    minRes = dp[i][j-1] + 1;
                    dpString[i][j] = dpString[i][j-1] + "+ " + aftStr.get(j-1) + "\n";
                }
                dp[i][j] = minRes;
            }
        }

        System.out.println("dp is " + dp[len1][len2]);
        return dpString[len1][len2];
    }

    public static void main(String[] args) throws IOException {
        File a = new File("C:\\Users\\zrc5\\Desktop\\test\\a.txt");
        File b = new File("C:\\Users\\zrc5\\Desktop\\test\\b.txt");
        compareFile(a,b);
    }
}