package Sort;

import java.util.Arrays;

public class ShellSort {
    public void shellSort(int[] nums) {
        for (int d = nums.length / 2; d >= 1; d /= 2) {
            for (int i = d; i < nums.length; i += d) {
                int temp = nums[i];
                int j;
                for (j = i; j > 0 && nums[j - d] > temp; j -= d) {
                    nums[j] = nums[j - d];
                }
                nums[j] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9};
        ShellSort sort = new ShellSort();
        sort.shellSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
