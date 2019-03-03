package Search.BinarySearch;

public class BinarySearch {
    public int binarySearch(int[] nums, int target) {
        int l = 0, r = nums.length - 1, mid;
        while (l <= r) {
            mid = l + (r - l) / 2;
            if (nums[mid] == target)
                return mid;
            if (nums[mid] > target)
                r = mid - 1;
            else
                l = mid + 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        BinarySearch binarySearch = new BinarySearch();
        int ans = binarySearch.binarySearch(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, 3);
        System.out.println(ans);
    }
}
