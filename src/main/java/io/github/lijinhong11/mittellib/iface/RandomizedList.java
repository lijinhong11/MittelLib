package io.github.lijinhong11.mittellib.iface;

import io.github.lijinhong11.mittellib.utils.random.FastRandom;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * A list for random selection
 *
 * @param <T> the type
 */
public interface RandomizedList<T> extends List<T> {
    @Nullable
    default T randomOne() {
        if (isEmpty()) {
            return null;
        }

        int index = FastRandom.nextInt(size());

        return get(index);
    }

    @Nullable
    default List<T> randomMulti(int size) {
        return randomMulti(size, false);
    }

    @Nullable
    default List<T> randomMulti(int size, boolean distinct) {
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
