package Search.PatternMatch;

public class BruteForce {
    public int bruteForce(String t, String p) {
        int i = 0, j = 0;
        while (i < t.length() - p.length() + 1 && j < p.length()) {
            if (t.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            } else {
                i = i - j + 1;
                j = 0;
            }
        }
        return j == p.length() ? i - j : -1;
    }

    public static void main(String[] args) {
        BruteForce bruteForce = new BruteForce();
        int ans = bruteForce.bruteForce("abcd", "bc");
        System.out.println(ans);
    }
}
