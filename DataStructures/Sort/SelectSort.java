package Sort;

import java.util.Arrays;

public class SelectSort {
    public void selectSort(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < nums[minIndex])
                    minIndex = j;
            }
            if (minIndex != i) {
                int t = nums[i];
                nums[i] = nums[minIndex];
                nums[minIndex] = t;
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9};
        SelectSort sort = new SelectSort();
        sort.selectSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
