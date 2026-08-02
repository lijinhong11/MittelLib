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

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * A list for random selection
 *
 * @param <T> the type
 */
public interface RandomizedList<T> extends List<T> {
    @Nullable default T randomOne() {
        if (isEmpty()) {
            return null;
        }

        int index = FastRandom.nextInt(size());

        return get(index);
    }

    @Nullable default List<T> randomMulti(int size) {
        return randomMulti(size, false);
    }

    @Nullable default List<T> randomMulti(int size, boolean distinct) {
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
