package com.legal.pipeline.domain.chain;

/**
 * Interface for validation handlers in the Chain of Responsibility pattern.
 */
public interface IValidationHandler {
    void setNextHandler(IValidationHandler nextHandler);
    boolean validate(String content, String fileName);
}
