package org.example.kitpo_l1;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryTreeBalanceTest {

    // Вспомогательный метод: дерево с IntType
    private BinaryTree makeIntTree(int... values) {
        BinaryTree t = new BinaryTree(new IntType().getTypeComparator());
        for (int v : values) {
            t.add(v);
        }
        return t;
    }

    // Проверка, что список отсортирован по возрастанию (для Integer)
    private boolean isSortedInt(List<Object> list) {
        for (int i = 1; i < list.size(); i++) {
            int a = (Integer) list.get(i - 1);
            int b = (Integer) list.get(i);
            if (a > b) return false;
        }
        return true;
    }

    // ====== Структурное + функциональное тестирование balance() ======

    @Test
    public void testEmptyTree() {
        BinaryTree t = makeIntTree();
        t.balance();
        assertEquals(0, t.size());
        assertTrue(t.toList().isEmpty());
    }

    @Test
    public void testSingleElement() {
        BinaryTree t = makeIntTree(10);
        t.balance();
        assertEquals(1, t.size());
        assertEquals(List.of(10), t.toList());
    }

    @Test
    public void testAllEqualValues() {
        BinaryTree t = makeIntTree(5, 5, 5, 5, 5);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(5, list.size());
        assertTrue(isSortedInt(list));
    }

    @Test
    public void testRandomUnordered() {
        BinaryTree t = makeIntTree(3, 1, 4, 1, 5, 9, 2);
        t.balance();
        List<Object> list = t.toList();
        assertTrue(isSortedInt(list));
        assertEquals(List.of(1, 1, 2, 3, 4, 5, 9), list);
    }

    @Test
    public void testAlreadySortedAscending() {
        BinaryTree t = makeIntTree(1, 2, 3, 4, 5);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(1, 2, 3, 4, 5), list);
    }

    @Test
    public void testReverseSorted() {
        BinaryTree t = makeIntTree(5, 4, 3, 2, 1);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(1, 2, 3, 4, 5), list);
    }

    @Test
    public void testWithRepeatingGroups() {
        // несколько групп повторяющихся элементов
        BinaryTree t = makeIntTree(3, 3, 1, 1, 2, 2, 2);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(1, 1, 2, 2, 2, 3, 3), list);
    }

    @Test
    public void testExtremeInMiddle() {
        // экстремальное значение в середине исходного набора
        BinaryTree t = makeIntTree(4, 2, 10, 3, 5);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(2, 3, 4, 5, 10), list);
    }

    @Test
    public void testExtremeAtBegin() {
        BinaryTree t = makeIntTree(1, 5, 4, 3, 2);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(1, 2, 3, 4, 5), list);
    }

    @Test
    public void testExtremeAtEnd() {
        BinaryTree t = makeIntTree(5, 4, 3, 2, 1);
        t.balance();
        List<Object> list = t.toList();
        assertEquals(List.of(1, 2, 3, 4, 5), list);
    }
}
