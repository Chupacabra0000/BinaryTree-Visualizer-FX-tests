package org.example.kitpo_l1;

public class MemoryDemo {

    public static void main(String[] args) {
        int n = 10000;
        Node.createdCount = 0;
        BinaryTree t = new BinaryTree(new IntType().getTypeComparator());
        for (int i = 0; i < n; i++) {
            t.add(i);
        }
        System.out.println("Nodes after insert: " + Node.createdCount);

        Node.createdCount = 0;
        t.balance();
        System.out.println("Nodes created during balance(): " + Node.createdCount);
    }
}