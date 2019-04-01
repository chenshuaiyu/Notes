package Sort;

import java.util.Arrays;

/**
 * 堆排序
 */
public class HeapSort {
    public void heapSort(int[] nums) {
        for (int i = nums.length / 2 - 1; i >= 0; i--)
            sift(nums, nums.length, i);
        for (int i = nums.length - 1; i > 0; i--) {
            swap(nums, 0, i);
            sift(nums, i, 0);
        }
    }

    private void sift(int[] nums, int len, int i) {
        while (i * 2 + 1 < len) {
            int child = i * 2 + 1;
            if (child + 1 < len && nums[child] < nums[child + 1]) ++child;
            if (nums[i] >= nums[child]) break;
            swap(nums, i, child);
            i = child;
        }
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9, 10};
        HeapSort sort = new HeapSort();
        sort.heapSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
