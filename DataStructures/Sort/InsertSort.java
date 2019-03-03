package Sort;

import java.util.Arrays;

public class InsertSort {
    public void insertSort(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            int t = nums[i], j;
            for (j = i; j > 0 && nums[j - 1] > t; j--) {
                nums[j] = nums[j - 1];
            }
            nums[j] = t;
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9};
        InsertSort sort = new InsertSort();
        sort.insertSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
