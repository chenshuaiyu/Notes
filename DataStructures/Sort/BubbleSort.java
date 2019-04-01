package Sort;

import java.util.Arrays;

/**
 * 冒泡排序
 */
public class BubbleSort {
    public void bubbleSort(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length - i - 1; j++) {
                if (nums[j] > nums[j + 1]) {
                    int t = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = t;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9, 10};
        BubbleSort sort = new BubbleSort();
        sort.bubbleSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
