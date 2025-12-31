package com.legal.pipeline.domain.chain;

/**
 * Abstract base class for validation handlers in the Chain of Responsibility
 * pattern.
 * Provides common functionality for chaining handlers.
 */
public abstract class AbstractValidationHandler implements IValidationHandler {

    protected IValidationHandler nextHandler;

    @Override
    public void setNextHandler(IValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Template method for validation. Subclasses implement specific validation
     * logic.
     *
     * @param documentId the ID of the document
     * @param content    the content to validate
     * @return true if validation passes
     */
    @Override
    public boolean validate(String documentId, String content) {
        if (performValidation(documentId, content)) {
            return nextHandler == null || nextHandler.validate(documentId, content);
        }
        return false;
    }

    /**
     * Abstract method for specific validation logic.
     *
     * @param documentId the ID of the document
     * @param content    the content to validate
     * @return true if this handler's validation passes
     */
    protected abstract boolean performValidation(String documentId, String content);
}
