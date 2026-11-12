package com.llmrouter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 规则路由：按请求里的 model 名称选择目标模型，未指定则用默认模型。
 * 后续阶段在这里加「按任务类型 / 成本 / 能力」的 if-else 规则，不引入策略模式。
 */
@Component
public class Router {

    private final String defaultModel;
    private final Map<String, LlmProperties.ModelConfig> models;
    private final OpenAiCompatibleClient client;

    public Router(LlmProperties properties, OpenAiCompatibleClient client) {
        this.defaultModel = properties.defaultModel();
        this.models = properties.models().stream()
                .collect(Collectors.toMap(LlmProperties.ModelConfig::name, Function.identity()));
        this.client = client;
    }

    public ChatResponse route(ChatRequest request) {
        if (request.prompt() == null || request.prompt().isBlank()) {
            throw new IllegalArgumentException("prompt 不能为空");
        }

        String name = (request.model() == null || request.model().isBlank())
                ? defaultModel
                : request.model();

        LlmProperties.ModelConfig config = models.get(name);
        if (config == null) {
            throw new IllegalArgumentException("未知模型: " + name);
        }
        return client.chat(config, request);
    }
}
