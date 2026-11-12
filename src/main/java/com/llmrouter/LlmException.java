package com.llmrouter;

/** 上游模型调用失败时抛出的业务异常。 */
public class LlmException extends RuntimeException {

    public LlmException(String message) {
        super(message);
    }
}
