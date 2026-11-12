package com.llmrouter;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 绑定 application.yml 里 llm 开头的配置。
 * 模型配置是「数据」而不是「类层级」，所以直接用一个 record 列表表示。
 */
@ConfigurationProperties(prefix = "llm")
public record LlmProperties(String defaultModel, List<ModelConfig> models) {

    public record ModelConfig(String name, String model, String baseUrl, String apiKey) {
    }
}
