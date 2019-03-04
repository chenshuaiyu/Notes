package Sort;

import java.util.Arrays;

public class MergeSort {
    public void mergeSort(int[] nums, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(nums, left, mid);
        mergeSort(nums, mid + 1, right);
        merge(nums, left, mid, right);
    }

    private void merge(int[] nums, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        System.arraycopy(nums, left, temp, 0, temp.length);
        int l = 0, r = mid - left + 1, m = mid - left;
        int index = left;
        while (l <= m && r <= right - left) {
            if (temp[l] < temp[r])
                nums[index] = temp[l++];
            else
                nums[index] = temp[r++];
            index++;
        }
        while (l <= m)
            nums[index++] = temp[l++];
        while (r <= right - left)
            nums[index++] = temp[r++];
    }

    public static void main(String[] args) {
        int[] nums = new int[]{8, 3, 5, 4, 1, 0, 2, 7, 6, 9};
        MergeSort sort = new MergeSort();
        sort.mergeSort(nums, 0, nums.length - 1);
        System.out.println(Arrays.toString(nums));
    }
}
