package io.github.lijinhong11.mittellib.objects;

import io.github.lijinhong11.mittellib.utils.random.FastRandom;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A list for random selection
 *
 * @param <T> the type
 */
public class RandomizedList<T> extends ArrayList<T> {
    public RandomizedList() {
        super();
    }

    public RandomizedList(int initialCapacity) {
        super(initialCapacity);
    }

    public RandomizedList(Collection<T> collection) {
        super(collection);
    }

    @SafeVarargs
    public RandomizedList(T... arr) {
        super(Arrays.asList(arr));
    }

    @Nullable
    public T randomOne() {
        if (isEmpty()) {
            return null;
        }

        int index = FastRandom.nextInt(size());

        return get(index);
    }

    @Nullable
    public List<T> randomMulti(int size) {
        return randomMulti(size, false);
    }

    @Nullable
    public List<T> randomMulti(int size, boolean distinct) {
        if (size > size()) {
            throw new IllegalArgumentException("size is bigger than the list's size");
        }

        List<T> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int index = FastRandom.nextInt(size());
            list.add(get(index));
        }

        return distinct ? list.stream().distinct().toList() : list;
    }
}
