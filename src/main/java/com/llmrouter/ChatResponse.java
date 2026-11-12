package com.llmrouter;

/**
 * 网关对外暴露的响应，把上游模型的用量信息一并带出，方便后续做可观测性。
 */
public record ChatResponse(
        String model,
        String content,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens) {
}
