package Sort;

import java.util.Arrays;

/**
 * 二分插入排序
 */
public class BinaryInsertSort {
    public void binaryInsertSort(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            int l = 0, r = i - 1, mid, temp = nums[i];
            while (l <= r) {
                mid = l + (r - l) / 2;
                if (nums[mid] > temp)
                    r = mid - 1;
                else
                    l = mid + 1;
            }
            for (int j = i - 1; j >= l; j--) {
                nums[j + 1] = nums[j];
            }
            nums[l] = temp;
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9, 10};
        BinaryInsertSort sort = new BinaryInsertSort();
        sort.binaryInsertSort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
