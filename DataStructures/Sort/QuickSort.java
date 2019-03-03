package Sort;

import java.util.Arrays;

public class QuickSort {
    public void quickSort(int[] nums, int left, int right) {
        if (left < right) {
            int i = left, j = right, temp = nums[left];
            while (i != j) {
                while (nums[j] > temp && i < j) j--;
                if (i < j)
                    nums[i++] = nums[j];
                while (nums[i] < temp && i < j) i++;
                if (i < j)
                    nums[j--] = nums[i];
            }
            nums[i] = temp;
            quickSort(nums, left, i - 1);
            quickSort(nums, i + 1, right);
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9};
        QuickSort sort = new QuickSort();
        sort.quickSort(nums, 0, nums.length - 1);
        System.out.println(Arrays.toString(nums));
    }
}
