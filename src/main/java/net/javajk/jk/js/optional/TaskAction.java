package net.javajk.jk.js.optional;


/**
 * funcional interface for javaScript task
 */
@FunctionalInterface
public interface TaskAction {
    Object run(String[] task);
}
