package ru.polytech.java.se;

import java.util.*;

public class AVLTree<E extends Comparable<E>> implements Set<E> {

    /**
     * Объект Узел. Имеет родительский узел и два потомка
     */
    private class Node {

        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        E value;
        Node left;
        Node right;
        Node parent;
        int balance;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private Node root;
    private int size = 0;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Проверка дерева на наличие вершин
     * @return {@code true} если дерево не содержит вершин
     */
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Проверка на наличие определенного объекта
     * @param o проверяемый объект
     * @return {@code true} если объект найден
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        E value = (E) o;
        return contains(value);
    }

    @Override
    public Iterator<E> iterator() {
        ArrayList<E> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        List<E> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list.toArray();
    }


    /**
     * Возвращает список с элементами дерева в порядке возрастания
     * @param curr колличество элементов дерева
     * @param list
     */
    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }


    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    /**
     * Поиск элемента по дереву
     * @param value значение искомого элемента
     * @return {@code true} если текущий элемент равен искомому
     */
    private boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("Значение равно null");
        }
        // ищем элемент
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                //сравниваем текущее значение элемента с тем, который ищем
                int cmp = compare(curr.value, value);
                // если они равны, возращаем true
                if (cmp == 0)
                    return true;
                    // если текущий элемент меньше искомого, то делаем текущим элементом правого потомка
                else if (cmp < 0)
                    curr = curr.right;
                    // иначе левого
                else
                    curr = curr.left;
            }
        }
        return false;
    }

    /**
     * Добавление элемента в дерево
     * @param value значение добавляемого элемента
     * @return {@code false} при  попытке добавления нулевого объекта
     */
    @Override
    public boolean add(E value) {
        if (root == null)
            root = new Node(value, null);
        else {
            Node n = root;
            Node parent;
            while (true) {
                int cmp = compare(n.value, value);
                if (cmp == 0)
                    return false;

                parent = n;

                boolean goLeft = cmp > 0;
                n = goLeft ? n.left : n.right;

                if (n == null) {
                    if (goLeft) {
                        parent.left = new Node(value, parent);
                    } else {
                        parent.right = new Node(value, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        size++;
        return true;
    }


    /**
     * Проверка на наличие определенного объекта
     * @param o проверяемый объект
     * @return {@code true} если объект найден
     */
    @Override
    public boolean remove(Object o) {
        E value = (E) o;
        return remove(value);
    }


    /**
     * Проверяет каждый элемент коллекции, есть ли он в дереве
     * @param c параметр коллекции
     * @return {@code true} если объект найдем
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        Object[] arr = c.toArray();
        for (Object item : arr) {
            if (!contains(item))
                return false;
        }
        return true;
    }

    /**
     * Добавляет все элементы коллекции в дерево
     * @param c элемент коллекции
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection<? extends E> c) {
        Object[] arr = c.toArray();
        int cTrue = 0;
        for (Object item : arr) {
            if (add((E) item))
                cTrue += 1;
        }
        return cTrue != 0;
    }

    /**
     * Сохраняет новый размер дерева
     * @param c элемент дерева, который есть в коллекции
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        List<E> tree = inorderTraverse();
        int oldSize = tree.size();
        tree.retainAll(c);
        int newSize = tree.size();

        root = null;
        size = 0;

        addAll(tree);

        int result = newSize - oldSize;

        return result != 0;
    }

    /**
     * Удаляет все элементы коллекции из дерева
     * @param c
     * @return
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        int cTrue = 0;
        for (Object item : arr) {
            if (remove(item))
                cTrue += 1;
        }
        return cTrue != 0;
    }

    /**
     * Обнуление корня дерева и его размера
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }


    private int height(Node n) {
        if (n == null)
            return -1;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    public int getHeight(){
        return height(root);
    }

    private void setBalance(Node... nodes){
        for (Node n : nodes) {
            n.balance = height(n.right) - height(n.left);
        }
    }

    private Node rotateLeft(Node a){
        Node b = a.right;
        b.parent = a.parent;
        a.right = b.left;
        if (a.right != null) {
            a.right.parent = a;
        }
        b.left = a;
        a.parent = b;
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }
        setBalance(a, b);
        return b;
    }

    private Node rotateRight(Node a) {

        Node b = a.left;
        b.parent = a.parent;

        a.left = b.right;

        if (a.left != null)
            a.left.parent = a;

        b.right = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }

        setBalance(a, b);

        return b;
    }

    // большой левый поворот
    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    // большой правый поворот
    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    /**
     * Выполняется балансировка дерева, при удалении или добавлении элемента
     * @param n узел, в котором выполняется балансировка
     */
    private void rebalance(Node n) {
        setBalance(n);

        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n);

        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }

        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }

    /**
     * Удаляет объект из дерева
     * @param value значение удаляемого объекта
     * @return {@code true} если объект найден, то он удаляется
     */
    private boolean remove(E value) {
        if (root == null)
            return false;
        Node n = root;
        Node parent = root;
        Node delNode = null;
        Node child = root;

        // ищем удаляемый элемент
        while (child != null) {
            parent = n;
            n = child;
            int cmp = compare(value, n.value);
            child = cmp >= 0 ? n.right : n.left;
            if (cmp == 0)
                delNode = n;
        }

        // если такой элемент есть, то удаляем его и возвращаем true
        if (delNode != null) {
            delNode.value = n.value;

            child = n.left != null ? n.left : n.right;

            if (compare(root.value, value) == 0) {
                root = child;
            } else {
                if (parent.left == n) {
                    parent.left = child;
                } else {
                    parent.right = child;
                }
                rebalance(parent);
            }
            size--;
            return true;
        }
        return false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "AVLT{" + root + "}";
    }

}
