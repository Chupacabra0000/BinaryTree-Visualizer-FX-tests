package org.example.kitpo_l1;


public class BalancePerformanceTest {

    private static BinaryTree makeRandomIntTree(int n) {
        BinaryTree t = new BinaryTree(new IntType().getTypeComparator());
        java.util.Random rnd = new java.util.Random(0);
        for (int i = 0; i < n; i++) {
            t.add(rnd.nextInt(n));
        }
        return t;
    }

    public static void main(String[] args) {
        int[] sizes = {1000, 3000, 5000, 10000, 20000, 50000};

        for (int n : sizes) {
            BinaryTree t = makeRandomIntTree(n);

            long start = System.nanoTime();
            t.balance();
            long end = System.nanoTime();

            double ms = (end - start) / 1_000_000.0;
            System.out.printf("N=%d -> %.3f ms%n", n, ms);
        }
    }
}
