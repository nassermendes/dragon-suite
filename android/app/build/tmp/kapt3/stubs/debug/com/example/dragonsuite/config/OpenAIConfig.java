package com.example.dragonsuite.config;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/example/dragonsuite/config/OpenAIConfig;", "", "()V", "openAI", "Lcom/aallam/openai/client/OpenAI;", "getClient", "initialize", "", "apiKey", "", "app_debug"})
public final class OpenAIConfig {
    @org.jetbrains.annotations.Nullable()
    private static com.aallam.openai.client.OpenAI openAI;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.dragonsuite.config.OpenAIConfig INSTANCE = null;
    
    private OpenAIConfig() {
        super();
    }
    
    public final void initialize(@org.jetbrains.annotations.NotNull()
    java.lang.String apiKey) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.aallam.openai.client.OpenAI getClient() {
        return null;
    }
}