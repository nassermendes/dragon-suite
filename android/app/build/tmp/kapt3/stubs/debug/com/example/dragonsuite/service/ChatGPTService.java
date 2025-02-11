package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\b0\u0015H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018J!\u0010\u0019\u001a\u00020\u00062\u0012\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\b0\u0015H\u0002\u00a2\u0006\u0002\u0010\u001cJ\u0006\u0010\u001d\u001a\u00020\fJH\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\b0\u00152\u0006\u0010\u001f\u001a\u00020\u000f2\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b#\u0010$J\u0006\u0010%\u001a\u00020&J\u000e\u0010\'\u001a\u00020&2\u0006\u0010!\u001a\u00020\u0006J\u0014\u0010(\u001a\u00020&2\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060\bJ\u000e\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u0006J\u000e\u0010,\u001a\u00020&2\u0006\u0010 \u001a\u00020\u0006J\u000e\u0010-\u001a\u00020&2\u0006\u0010\u001f\u001a\u00020\u000fJ\u001c\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00060/2\u0006\u00100\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u00101J\"\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\b0\u0015H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b3\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00064"}, d2 = {"Lcom/example/dragonsuite/service/ChatGPTService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "currentDescription", "", "currentHashtags", "", "currentScheduledTime", "Ljava/time/LocalDateTime;", "currentState", "Lcom/example/dragonsuite/service/ConversationState;", "currentTitle", "currentVideoUri", "Landroid/net/Uri;", "openAI", "Lcom/aallam/openai/client/OpenAI;", "videoPostManager", "Lcom/example/dragonsuite/service/VideoPostManager;", "confirmAndPost", "Lkotlin/Result;", "Lcom/example/dragonsuite/model/UploadResult;", "confirmAndPost-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "formatResults", "results", "Lcom/example/dragonsuite/model/ConnectionTestResult;", "(Ljava/lang/Object;)Ljava/lang/String;", "getCurrentState", "postVideo", "videoUri", "title", "description", "hashtags", "postVideo-yxL6bBk", "(Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resetState", "", "setDescription", "setHashtags", "setScheduledTime", "", "timeStr", "setTitle", "startVideoAnalysis", "streamResponse", "Lkotlinx/coroutines/flow/Flow;", "userMessage", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "testConnections", "testConnections-IoAF18A", "app_debug"})
public final class ChatGPTService {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.aallam.openai.client.OpenAI openAI = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.dragonsuite.service.VideoPostManager videoPostManager = null;
    @org.jetbrains.annotations.NotNull()
    private com.example.dragonsuite.service.ConversationState currentState = com.example.dragonsuite.service.ConversationState.IDLE;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri currentVideoUri;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentTitle;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentDescription;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<java.lang.String> currentHashtags;
    @org.jetbrains.annotations.Nullable()
    private java.time.LocalDateTime currentScheduledTime;
    
    public ChatGPTService(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object streamResponse(@org.jetbrains.annotations.NotNull()
    java.lang.String userMessage, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlinx.coroutines.flow.Flow<java.lang.String>> $completion) {
        return null;
    }
    
    public final void startVideoAnalysis(@org.jetbrains.annotations.NotNull()
    android.net.Uri videoUri) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.dragonsuite.service.ConversationState getCurrentState() {
        return null;
    }
    
    public final void resetState() {
    }
    
    public final void setTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String title) {
    }
    
    public final void setDescription(@org.jetbrains.annotations.NotNull()
    java.lang.String description) {
    }
    
    public final void setHashtags(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> hashtags) {
    }
    
    public final boolean setScheduledTime(@org.jetbrains.annotations.NotNull()
    java.lang.String timeStr) {
        return false;
    }
    
    private final java.lang.String formatResults(java.lang.Object results) {
        return null;
    }
}