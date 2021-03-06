/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package org.serversass;

import org.serversass.ast.Color;
import org.serversass.ast.Expression;
import org.serversass.ast.FunctionCall;

/**
 * Contains all functions which can be called from sass.
 */
@SuppressWarnings("squid:S1172")
public class Functions {

    private Functions() {
    }

    private static Expression changeLighteness(Color color, int changeInPercent) {
        Color.HSL hsl = color.getHSL();
        hsl.setL(Math.max(Math.min(hsl.getL() * (1 + (changeInPercent / 100d)), 1d), 0d));
        return hsl.getColor();
    }

    private static Expression changeSaturation(Color color, int changeInPercent) {
        Color.HSL hsl = color.getHSL();
        hsl.setS(Math.max(Math.min(hsl.getS() * (1 + (changeInPercent / 100d)), 1d), 0d));
        return hsl.getColor();
    }

    /**
     * Creates a color from given RGB values.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression rgb(Generator generator, FunctionCall input) {
        return new Color(input.getExpectedIntParam(0), input.getExpectedIntParam(1), input.getExpectedIntParam(2));
    }

    /**
     * Creates a color from given RGB and alpha values.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression rgba(Generator generator, FunctionCall input) {
        return new Color(input.getExpectedIntParam(0),
                         input.getExpectedIntParam(1),
                         input.getExpectedIntParam(2),
                         input.getExpectedFloatParam(3));
    }

    /**
     * Adjusts the hue of the given color by the given number of degrees.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression adjusthue(Generator generator, FunctionCall input) {
        Color color = input.getExpectedColorParam(0);
        int changeInDegrees = input.getExpectedIntParam(1);
        Color.HSL hsl = color.getHSL();
        hsl.setH(hsl.getH() + changeInDegrees);
        return hsl.getColor();
    }

    /**
     * Increases the lightness of the given color by N percent.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression lighten(Generator generator, FunctionCall input) {
        Color color = input.getExpectedColorParam(0);
        int increase = input.getExpectedIntParam(1);
        return changeLighteness(color, increase);
    }

    /**
     * Decreases the lightness of the given color by N percent.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression darken(Generator generator, FunctionCall input) {
        Color color = input.getExpectedColorParam(0);
        int decrease = input.getExpectedIntParam(1);
        return changeLighteness(color, -decrease);
    }

    /**
     * Increases the saturation of the given color by N percent.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression saturate(Generator generator, FunctionCall input) {
        Color color = input.getExpectedColorParam(0);
        int increase = input.getExpectedIntParam(1);
        return changeSaturation(color, increase);
    }

    /**
     * Decreases the saturation of the given color by N percent.
     *
     * @param generator the surrounding generator
     * @param input     the function call to evaluate
     * @return the result of the evaluation
     */
    public static Expression desaturate(Generator generator, FunctionCall input) {
        Color color = input.getExpectedColorParam(0);
        int decrease = input.getExpectedIntParam(1);
        return changeSaturation(color, -decrease);
    }
}
