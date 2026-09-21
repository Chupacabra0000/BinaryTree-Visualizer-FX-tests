package org.example.kitpo_l1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryTreeWhiteBoxTest {

    private BinaryTree makeSkewedIntTree(int n) {
        BinaryTree t = new BinaryTree(new IntType().getTypeComparator());
        // вставляем по возрастанию → сильно перекошенное дерево (почти список)
        for (int i = 0; i < n; i++) {
            t.add(i);
        }
        return t;
    }

    // рекурсивный подсчёт высоты дерева
    private int height(Node n) {
        if (n == null) return 0;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    @Test
    public void testHeightImprovesAfterBalance() {
        int n = 100;
        BinaryTree t = makeSkewedIntTree(n);

        int hBefore = height(t.getRoot());
        t.balance();
        int hAfter = height(t.getRoot());

        // до балансировки высота примерно n (цепочка)
        // после — около log2(n)
        assertTrue(hBefore > hAfter, "Высота должна уменьшиться после balance()");
        assertTrue(hAfter <= 2 * (int)(Math.log(n) / Math.log(2)) + 2,
                "Высота после балансировки должна быть около O(log N)");
    }
}
