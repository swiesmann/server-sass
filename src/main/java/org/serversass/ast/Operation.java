/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package org.serversass.ast;

import org.serversass.Generator;
import org.serversass.Scope;

import java.util.Locale;

/**
 * Represents a binary operation.
 */
public class Operation implements Expression {
    private String operation;
    private Expression left;
    private Expression right;
    private boolean protect = false;

    /**
     * Creates a new operation, with the given operator and the left and right expression.
     *
     * @param operation the operation to use
     * @param left      the expression on the left side
     * @param right     the expression on the right side
     */
    public Operation(String operation, Expression left, Expression right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    /**
     * Marks that this expression is guarded by braces so that operator precedence does not matter.
     */
    public void protect() {
        protect = true;
    }

    /**
     * Returns the operator of this operation.
     *
     * @return the operator as string
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Determines if the operation is guarded by braces and most not be re-ordered by operator precedence.
     *
     * @return <tt>true</tt> if the operation is surrounded by braces, <tt>false</tt> otherwise
     */
    public boolean isProtect() {
        return protect;
    }

    /**
     * Returns the left side of the operation.
     *
     * @return the left side of the operation
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Returns the right side of the operation.
     *
     * @return the right side of the operation
     */
    public Expression getRight() {
        return right;
    }

    /**
     * Sets the right side of the operation.
     *
     * @param right the new right side of the operation
     */
    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return (protect ? "(" : "") + left + " " + operation + " " + right + (protect ? ")" : "");
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public Expression eval(Scope scope, Generator gen) {
        Expression newLeft = left.eval(scope, gen);
        Expression newRight = right.eval(scope, gen);
        if ((newLeft instanceof Number) && (newRight instanceof Number)) {
            Number l = (Number) newLeft;
            Number r = (Number) newRight;

            double lVal = l.getNumericValue();
            String lUnit = l.getUnit();
            if ("%".equals(lUnit)) {
                lVal /= 100d;
                lUnit = "";
            }
            double rVal = r.getNumericValue();
            String rUnit = r.getUnit();
            if ("%".equals(rUnit)) {
                rVal /= 100d;
                rUnit = "";
            }

            double value = evalOperation(gen, lVal, rVal);

            String unit = "";
            if (!"/".equals(operation)) {
                if ("%".equals(l.getUnit()) && ("%".equals(r.getUnit()) || r.getUnit() != null && r.getUnit().isEmpty())
                    || l.getUnit() != null && l.getUnit().isEmpty() && "%".equals(r.getUnit())) {
                    value *= 100;
                    unit = "%";
                } else {
                    unit = lUnit;
                    if (unit != null && unit.isEmpty()) {
                        unit = rUnit;
                    } else if (rUnit != null && !rUnit.isEmpty() && !rUnit.equals(lUnit)) {
                        gen.warn(String.format("Incompatible units mixed in expression '%s': Using left unit for result",
                                               this));
                    }
                }
            }
            double rounded = Math.round(value);
            if (Math.abs(value - rounded) > 0.009) {
                return new Number(value, String.format(Locale.ENGLISH, "%1.2f", value), unit);
            }
            return new Number(value, String.valueOf(Math.round(value)), unit);
        } else {
            return new Value(newLeft.toString() + newRight.toString());
        }
    }

    protected double evalOperation(Generator gen, double lVal, double rVal) {
        double value = 0d;
        if ("/".equals(operation)) {
            if (rVal != 0) {
                value = lVal / rVal;
            } else {
                gen.warn(String.format("Cannot evaluate: '%s': division by 0. Defaulting to 0 as result", this));
            }
        } else if ("*".equals(operation)) {
            value = lVal * rVal;
        } else if ("%".equals(operation)) {
            value = lVal % rVal;
        } else if ("+".equals(operation)) {
            value = lVal + rVal;
        } else if ("-".equals(operation)) {
            value = lVal - rVal;
        }
        return value;
    }
}
