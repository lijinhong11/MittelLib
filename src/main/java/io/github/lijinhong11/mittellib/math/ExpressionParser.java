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
package io.github.lijinhong11.mittellib.math;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.parser.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class ExpressionParser {
    private static final Map<String, FunctionIfc> IMPORTED_FUNCTIONS = new ConcurrentHashMap<>();

    private ExpressionParser() {}

    public static boolean forBooleanResult(@NotNull String expression) throws EvaluationException, ParseException {
        return forBooleanResult(expression, Map.of());
    }

    public static boolean forBooleanResult(@NotNull String expression, @NotNull Map<String, ?> variables)
            throws EvaluationException, ParseException {
        return evaluate(expression, variables).getBooleanValue();
    }

    public static double forDoubleResult(@NotNull String expression) throws EvaluationException, ParseException {
        return forDoubleResult(expression, Map.of());
    }

    public static double forDoubleResult(@NotNull String expression, @NotNull Map<String, ?> variables)
            throws EvaluationException, ParseException {
        return evaluate(expression, variables).getNumberValue().doubleValue();
    }

    public static int forIntResult(@NotNull String expression) throws EvaluationException, ParseException {
        return forIntResult(expression, Map.of());
    }

    public static int forIntResult(@NotNull String expression, @NotNull Map<String, ?> variables)
            throws EvaluationException, ParseException {
        return evaluate(expression, variables).getNumberValue().intValue();
    }

    public static @NotNull EvaluationValue evaluate(@NotNull String expression)
            throws EvaluationException, ParseException {
        return evaluate(expression, Map.of());
    }

    @SuppressWarnings("unchecked")
    public static @NotNull EvaluationValue evaluate(@NotNull String expression, @NotNull Map<String, ?> variables)
            throws EvaluationException, ParseException {
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(variables, "variables");

        ExpressionConfiguration configuration = ExpressionConfiguration.defaultConfiguration();
        Map.Entry<String, FunctionIfc>[] functions = IMPORTED_FUNCTIONS.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .toArray(Map.Entry[]::new);
        if (functions.length > 0) {
            configuration = configuration.withAdditionalFunctions(functions);
        }

        return new Expression(expression, configuration).withValues(variables).evaluate();
    }

    public static void importFunction(@NotNull String name, @NotNull FunctionIfc function) {
        importFunction(name, function, false);
    }

    public static void importFunction(@NotNull String name, @NotNull FunctionIfc function, boolean replaceExisting) {
        String normalizedName = normalizeFunctionName(name);
        Objects.requireNonNull(function, "function");

        if (!replaceExisting && isFunctionDefined(normalizedName)) {
            throw new IllegalArgumentException("Function is already defined: " + normalizedName);
        }
        IMPORTED_FUNCTIONS.put(normalizedName, function);
    }

    public static void importFunctions(@NotNull Map<String, ? extends FunctionIfc> functions) {
        Objects.requireNonNull(functions, "functions");
        functions.forEach(ExpressionParser::importFunction);
    }

    public static boolean removeImportedFunction(@NotNull String name) {
        return IMPORTED_FUNCTIONS.remove(normalizeFunctionName(name)) != null;
    }

    public static void clearImportedFunctions() {
        IMPORTED_FUNCTIONS.clear();
    }

    public static boolean isFunctionDefined(@NotNull String name) {
        String normalizedName = normalizeFunctionName(name);
        return IMPORTED_FUNCTIONS.containsKey(normalizedName)
                || ExpressionConfiguration.defaultConfiguration()
                                .getFunctionDictionary()
                                .getFunction(normalizedName)
                        != null;
    }

    private static @NotNull String normalizeFunctionName(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        String normalizedName = name.strip().toUpperCase(Locale.ROOT);
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be blank");
        }
        return normalizedName;
    }
}
