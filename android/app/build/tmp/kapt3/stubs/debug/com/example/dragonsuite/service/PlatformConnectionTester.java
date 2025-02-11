package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\r\u001a\u00020\u000b2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fJ\u001c\u0010\u0011\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0012\u001a\u00020\t2\b\u0010\u0013\u001a\u0004\u0018\u00010\u000bH\u0002J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\nH\u0082@\u00a2\u0006\u0002\u0010\u0018R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\u0007\u001a\u001a\u0012\u0004\u0012\u00020\t\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\f\u001a\u001a\u0012\u0004\u0012\u00020\t\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/dragonsuite/service/PlatformConnectionTester;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "client", "Lokhttp3/OkHttpClient;", "platformEndpoints", "", "Lcom/example/dragonsuite/model/Platform;", "Lcom/example/dragonsuite/model/Account;", "", "platformTokens", "formatResults", "results", "", "Lcom/example/dragonsuite/model/ConnectionTestResult;", "parseAccountInfo", "platform", "response", "testAllConnections", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "testConnection", "account", "(Lcom/example/dragonsuite/model/Platform;Lcom/example/dragonsuite/model/Account;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class PlatformConnectionTester {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<com.example.dragonsuite.model.Platform, java.util.Map<com.example.dragonsuite.model.Account, java.lang.String>> platformEndpoints = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<com.example.dragonsuite.model.Platform, java.util.Map<com.example.dragonsuite.model.Account, java.lang.String>> platformTokens = null;
    
    public PlatformConnectionTester(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object testAllConnections(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.dragonsuite.model.ConnectionTestResult>> $completion) {
        return null;
    }
    
    private final java.lang.Object testConnection(com.example.dragonsuite.model.Platform platform, com.example.dragonsuite.model.Account account, kotlin.coroutines.Continuation<? super com.example.dragonsuite.model.ConnectionTestResult> $completion) {
        return null;
    }
    
    private final java.lang.String parseAccountInfo(com.example.dragonsuite.model.Platform platform, java.lang.String response) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatResults(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.dragonsuite.model.ConnectionTestResult> results) {
        return null;
    }
}