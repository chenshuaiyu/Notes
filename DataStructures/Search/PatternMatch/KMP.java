package Search.PatternMatch;

/**
 * 快速字符串匹配算法
 */
public class KMP {
    private int[] getNext(String p) {
        int[] next = new int[p.length() + 1];
        next[0] = -1;
        int i = 0, j = -1;
        while (i < p.length()) {
            if (j == -1 || p.charAt(i) == p.charAt(j)) {
                i++;
                j++;
                next[i] = j;
            } else {
                j = next[j];
            }
        }
        return next;
    }

    public int kmp(String t, String p) {
        int[] next = getNext(p);
        int i = 0, j = 0;
        while (i < t.length() && j < p.length()) {
            if (j == -1 || t.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        if (j == p.length()) return i - p.length();
        return -1;
    }

    public static void main(String[] args) {
        KMP kmp = new KMP();
        int ans = kmp.kmp("abcd", "bc");
        System.out.println(ans);
    }
}
