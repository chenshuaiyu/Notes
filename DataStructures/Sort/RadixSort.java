package Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基数排序
 */
public class RadixSort {
    public void radixSort(int[] nums, int maxDigit) {
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            res.add(new ArrayList<>());
        }
        for (int i = 1; i <= maxDigit; i *= 10) {
            for (int n : nums) {
                int index = (n / i) % 10;
                res.get(index).add(n);
            }
            int index = 0;
            for (int j = 0; j < res.size(); j++) {
                for (int k = 0; k < res.get(j).size(); k++) {
                    nums[index++] = res.get(j).get(k);
                }
                res.get(j).clear();
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{312, 126, 272, 226, 8, 165, 123, 12, 28};
        RadixSort sort = new RadixSort();
        sort.radixSort(nums, 100);
        System.out.println(Arrays.toString(nums));
    }
}
