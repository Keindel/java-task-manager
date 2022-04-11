import tasks.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    static final int MAX_LENGTH = 10;
    Map<Integer, Node<Task>> historyMap = new HashMap<>();

    class Node<E> {
        Node<E> prev;
        E data;
        Node<E> next;

        Node(Node<E> prev, E data, Node<E> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        tail = new Node<>(oldTail, task, null);
        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
        }
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        if (head == null) {
            return List.of();
            //throw new NoSuchElementException("there's no head Node in history");
        }
        Node<Task> iterator = head;
        for (int i = 0; i < historyMap.size(); i++) {
            history.add(iterator.data);
            iterator = iterator.next;
        }
        return history;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;
        if (node == head && node == tail) {
            head = null;
            tail = null;
            return;
        }
        if (node != head && node != tail) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node == head) {
            head = node.next;
            head.prev = null;
        } else {
            tail = node.prev;
            tail.next = null;
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public void add(Task task) {
        // Если узел с этой задачей уже есть - удаляем из связного списка
        removeNode(historyMap.get(task.getId()));
        linkLast(task);
        historyMap.put(task.getId(), this.tail);
        // Если теперь в HashMap > MAX_LENGTH элементов, то удаляем первый элемент из списка и соответсвующий из HashMap
        if (historyMap.size() > MAX_LENGTH) {
            historyMap.remove(this.head);
            removeNode(this.head);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + getHistory() +
                '}';
    }

    static String toStringOfIds(HistoryManager manager) {
        StringJoiner joiner = new StringJoiner(",");
        for (Task task : manager.getHistory()) {
            joiner.add(String.valueOf(task.getId()));
        }
        return joiner.toString();
    }

    static List<Integer> fromString(String value) {
        List<Integer> historyFromStringList = new ArrayList<>();
        if (value.isBlank()) {
            return historyFromStringList;
        }
        String[] historyAsArray = value.split(",");
        for (String s : historyAsArray) {
            historyFromStringList.add(Integer.parseInt(s));
        }
        return historyFromStringList;
    }
}