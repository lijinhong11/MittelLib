/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.utils.random;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

@SuppressWarnings("deprecation")
public class WeightedRandomMap<K> extends Object2DoubleOpenHashMap<K> {
    private List<K> keys;
    private double[] probability;
    private int[] alias;

    private boolean dirty = true;

    public WeightedRandomMap() {
        super();
    }

    public WeightedRandomMap(Map<K, Double> map) {
        super(map);
    }

    public WeightedRandomMap(Object2DoubleMap<K> map) {
        super(map);
    }

    @Override
    public double put(K key, double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive and not null");
        }

        super.put(key, weight);
        dirty = true;

        return weight;
    }

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
        return getOrDefault(key, 0d);
    }

    public double getProbability(K key) {
        double weight = getWeight(key);

        if (isEmpty() || weight <= 0d) {
            return 0d;
        }

        double totalWeight = DoubleStream.of(values.toDoubleArray()).sum();

        if (totalWeight <= 0) {
            return 0d;
        }

        return weight / totalWeight;
    }

    public String getDisplayProbability(K key) {
        return String.format("%.2f", getProbability(key));
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
