package ru.polytech.java.se;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AVLTreeTest {

    @Test
    public void size() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();
        assertThat(tree.size(), is(0));

        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(3);
        assertThat(tree.size(), is(4));

        tree.add(2);
        tree.add(4);
        tree.add(5);
        assertThat(tree.size(), is(5));
    }

    @Test
    public void isEmpty() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();
        assertThat(tree.isEmpty(), is(true));

        tree.add(1);
        tree.add(2);
        tree.add(3);
        assertThat(tree.isEmpty(), is(false));
    }

    @Test
    public void contains() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        assertThat(tree.contains(1), is(true));
        assertThat(tree.contains(3), is(true));
        assertThat(tree.contains(9), is(false));
        assertThat(tree.contains(12), is(false));
    }

    @Test
    public void add() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.add(1);
        tree.add(-1);
        tree.add(3);
        tree.add(-4);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        assertThat(tree.contains(1), is(true));
        assertThat(tree.contains(-1), is(true));
        assertThat(tree.contains(3), is(true));
        assertThat(tree.contains(9), is(false));
        assertThat(tree.contains(12), is(false));
        assertThat(tree.size(), is(8));
    }

    @Test
    public void remove() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.add(1);
        tree.add(-1);
        tree.add(3);
        tree.add(-4);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        assertThat(tree.size(), is(8));
        assertThat(tree.contains(1), is(true));
        assertThat(tree.contains(7), is(true));

        // удаляем лист
        tree.remove(8);

        assertThat(tree.size(), is(7));
        assertThat(tree.contains(8), is(false));

        // удаляем промежуточный узел с одним потомком
        assertThat(tree.contains(-1), is(true));
        tree.remove(-1);
        assertThat(tree.size(), is(6));
        assertThat(tree.contains(-1), is(false));

        // удаляем промежуточный узел с двумя потомками
        assertThat(tree.contains(1), is(true));
        tree.remove(1);
        assertThat(tree.size(), is(5));
        assertThat(tree.contains(1), is(false));

        // удаляем корень
        assertThat(tree.contains(5), is(true));
        tree.remove(5);
        assertThat(tree.size(), is(4));
        assertThat(tree.contains(5), is(false));

        // удаляем несуществующий элемент
        boolean result = tree.remove(1000);
        assertThat(result, is(false));
        assertThat(tree.size(), is(4));
    }

    @Test
    public void clear() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();

        for (int i = -50; i < 100; i++) {
            tree.add(i);
        }

        assertThat(tree.size(), is(150));

        tree.clear();
        assertThat(tree.size(), is(0));
    }

    @Test
    public void testForComparator() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>((v1, v2) -> {
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });

        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        List<Integer> real = tree.inorderTraverse();
        List<Integer> expected = new ArrayList<>();
        expected.add(2);
        expected.add(4);
        expected.add(6);
        expected.add(8);
        expected.add(1);
        expected.add(3);
        expected.add(5);
        expected.add(7);

        assertThat(real, is(expected));
    }

    @Test
    public void containsAll() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();
        List<Integer> list = new ArrayList<>();

        tree.add(1);
        tree.add(2);
        tree.add(3);

        boolean result = tree.containsAll(list);
        assertThat(result, is(true));

        list.add(1);
        list.add(2);

        result = tree.containsAll(list);
        assertThat(result, is(true));

        list.add(4);

        result = tree.containsAll(list);
        assertThat(result, is(false));
    }

    @Test
    public void addAll() throws Exception {
        Random rnd = new Random();
        AVLTree<Integer> tree = new AVLTree<>();
        AVLTree<Integer> tree1 = new AVLTree<>();
        List<Integer> list = new ArrayList<>();

        int temp = 0;
        for (int i = 0; i < 20; i++) {
            temp = rnd.nextInt(50);
            tree1.add(temp);
            list.add(temp);
        }

        tree.addAll(list);

        assertThat(tree.inorderTraverse(), is(tree1.inorderTraverse()));

        tree.clear();
        list.clear();

        tree.add(1);
        tree.add(2);
        tree.add(3);

        list.add(1);
        list.add(2);
        list.add(3);

        boolean result = tree.addAll(list);
        assertThat(result, is(false));

        list.add(4);

        result = tree.addAll(list);
        assertThat(result, is(true));
    }

    @Test
    public void removeAll() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();
        List<Integer> list = new ArrayList<>();

        tree.add(1);
        tree.add(2);
        tree.add(3);

        boolean result = tree.removeAll(list);
        assertThat(result, is(false));

        list.add(1);
        list.add(2);

        result = tree.removeAll(list);
        assertThat(result, is(true));
        assertThat(tree.size(), is(1));
        assertThat(tree.contains(1), is(false));
        assertThat(tree.contains(3), is(true));

        list.add(3);
        result = tree.removeAll(list);
        assertThat(result, is(true));
        assertThat(tree.size(), is(0));
        assertThat(tree.contains(3), is(false));
    }

    @Test
    public void retainAll() throws Exception {
        AVLTree<Integer> tree = new AVLTree<>();
        List<Integer> list = new ArrayList<>();

        tree.add(1);
        tree.add(2);
        tree.add(3);

        list.add(1);
        list.add(2);
        list.add(3);

        boolean result = tree.retainAll(list);
        assertThat(result, is(false));
        assertThat(tree.size(), is(3));
        assertThat(tree.contains(3), is(true));
        assertThat(tree.contains(2), is(true));
        assertThat(tree.contains(1), is(true));

        list.remove((Integer) 3);
        result = tree.retainAll(list);
        assertThat(result, is(true));
        assertThat(tree.size(), is(2));
        assertThat(tree.contains(3), is(false));
        assertThat(tree.contains(2), is(true));
        assertThat(tree.contains(1), is(true));
    }
}