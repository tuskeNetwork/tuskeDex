/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package tuskex.cli.table.column;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static tuskex.cli.table.column.Column.JUSTIFICATION.RIGHT;

/**
 * For displaying Integer values.
 */
public class IntegerColumn extends NumberColumn<IntegerColumn, Integer> {

    protected final List<Integer> rows = new ArrayList<>();

    protected final Predicate<String> isNewMaxWidth = (s) -> s != null && !s.isEmpty() && s.length() > maxWidth;

    // The default IntegerColumn JUSTIFICATION is RIGHT.
    public IntegerColumn(String name) {
        this(name, RIGHT);
    }

    public IntegerColumn(String name, JUSTIFICATION justification) {
        super(name, justification);
        this.maxWidth = name.length();
    }

    @Override
    public void addRow(Integer value) {
        rows.add(value);

        String s = String.valueOf(value);
        if (isNewMaxWidth.test(s))
            maxWidth = s.length();
    }

    @Override
    public List<Integer> getRows() {
        return rows;
    }

    @Override
    public int rowCount() {
        return rows.size();
    }

    @Override
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    @Override
    public Integer getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public void updateRow(int rowIndex, Integer newValue) {
        rows.set(rowIndex, newValue);
    }

    @Override
    public String getRowAsFormattedString(int rowIndex) {
        String s = String.valueOf(getRow(rowIndex));
        return toJustifiedString(s);
    }

    @Override
    public StringColumn asStringColumn() {
        IntStream.range(0, rows.size()).forEachOrdered(rowIndex ->
                stringColumn.addRow(getRowAsFormattedString(rowIndex)));

        return stringColumn;
    }
}
