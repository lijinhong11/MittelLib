package io.github.lijinhong11.mittellib.utils.random;

import java.util.*;

public class WeightedRandomMap<K> extends LinkedHashMap<K, Double> {
    private List<K> keys;
    private double[] probability;
    private int[] alias;

    private boolean dirty = true;

    @Override
    public Double put(K key, Double weight) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive and not null");
        }

        super.put(key, weight);
        dirty = true;

        return weight;
    }

    @Override
    public Double remove(Object key) {
        Double weight = super.remove(key);

        if (weight != null) {
            dirty = true;
        }

        return weight;
    }

    @Override
    public void clear() {
        super.clear();
        dirty = true;
    }

    public double getWeight(K key) {
        return getOrDefault(key, 0.0);
    }

    public K randomOne() {
        if (isEmpty()) {
            throw new IllegalStateException("WeightedRandomMap is empty");
        }

        if (dirty) {
            rebuild();
        }

        int column = FastRandom.nextInt(keys.size());

        boolean coinToss = FastRandom.nextDouble() < probability[column];

        int index = coinToss ? column : alias[column];

        return keys.get(index);
    }

    private void rebuild() {
        int n = size();

        keys = new ArrayList<>(keySet());

        probability = new double[n];
        alias = new int[n];

        double total = values().stream().mapToDouble(Double::doubleValue).sum();

        double[] scaled = new double[n];

        int i = 0;
        for (double weight : values()) {
            scaled[i++] = weight * n / total;
        }

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        for (i = 0; i < n; i++) {
            if (scaled[i] < 1.0) {
                small.add(i);
            } else {
                large.add(i);
            }
        }

        while (!small.isEmpty() && !large.isEmpty()) {
            int less = small.removeLast();
            int more = large.removeLast();

            probability[less] = scaled[less];
            alias[less] = more;

            scaled[more] = (scaled[more] + scaled[less]) - 1;

            if (scaled[more] < 1.0) {
                small.add(more);
            } else {
                large.add(more);
            }
        }

        while (!large.isEmpty()) {
            probability[large.removeLast()] = 1.0;
        }

        while (!small.isEmpty()) {
            probability[small.removeLast()] = 1.0;
        }

        dirty = false;
    }
}
