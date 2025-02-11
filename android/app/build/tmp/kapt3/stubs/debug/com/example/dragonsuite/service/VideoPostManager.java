package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0010\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0012\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00180\u001dJ\u0012\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00180\u001dJP\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0\u00180!2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u0018H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\'\u0010(J*\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0\u00180!2\u0006\u0010*\u001a\u00020+H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b,\u0010-J*\u0010.\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0\u00180!2\u0006\u0010*\u001a\u00020+H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b/\u0010-J:\u00100\u001a\b\u0012\u0004\u0012\u00020\"0\u00182\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u0018H\u0086@\u00a2\u0006\u0002\u00101J<\u00102\u001a\u00020+2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u0018H\u0086@\u00a2\u0006\u0002\u0010(JR\u00103\u001a\b\u0012\u0004\u0012\u00020+0!2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u00182\u0006\u00104\u001a\u000205H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b6\u00107J\"\u00108\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002090\u00180!H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b:\u0010\u000fJ\u001e\u0010;\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0082@\u00a2\u0006\u0002\u0010<JD\u0010=\u001a\u00020\"2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010>\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u0018H\u0082@\u00a2\u0006\u0002\u0010?JD\u0010=\u001a\u00020\"2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010>\u001a\u00020\u00192\u0006\u0010@\u001a\u00020\u00122\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020$0\u0018H\u0082@\u00a2\u0006\u0002\u0010AJ\u001c\u0010B\u001a\b\u0012\u0004\u0012\u00020\u000e0!H\u0082@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bC\u0010\u000fR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006D"}, d2 = {"Lcom/example/dragonsuite/service/VideoPostManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "client", "Lokhttp3/OkHttpClient;", "database", "Lcom/example/dragonsuite/database/AppDatabase;", "platformTester", "Lcom/example/dragonsuite/service/PlatformConnectionTester;", "videoPostDao", "Lcom/example/dragonsuite/database/dao/VideoPostDao;", "clearQueue", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearScheduled", "createTempFile", "Ljava/io/File;", "contentResolver", "Landroid/content/ContentResolver;", "videoUri", "Landroid/net/Uri;", "getAccountsForPlatform", "", "Lcom/example/dragonsuite/service/Account;", "platform", "Lcom/example/dragonsuite/service/Platform;", "getQueuedPosts", "Lkotlinx/coroutines/flow/Flow;", "Lcom/example/dragonsuite/model/VideoPost;", "getScheduledPosts", "postNow", "Lkotlin/Result;", "Lcom/example/dragonsuite/service/UploadResult;", "title", "", "description", "hashtags", "postNow-hUnOzRk", "(Landroid/content/ContentResolver;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "postQueuedVideo", "id", "", "postQueuedVideo-gIAlu-s", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "postScheduledVideo", "postScheduledVideo-gIAlu-s", "postVideo", "(Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "queueVideo", "scheduleVideo", "scheduledTime", "Ljava/time/LocalDateTime;", "scheduleVideo-bMdYcbs", "(Landroid/content/ContentResolver;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/time/LocalDateTime;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "testConnections", "Lcom/example/dragonsuite/service/ConnectionTestResult;", "testConnections-IoAF18A", "testVideoUpload", "(Landroid/content/ContentResolver;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadToPlatform", "account", "(Lcom/example/dragonsuite/service/Platform;Lcom/example/dragonsuite/service/Account;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "videoFile", "(Lcom/example/dragonsuite/service/Platform;Lcom/example/dragonsuite/service/Account;Ljava/io/File;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validateConnections", "validateConnections-IoAF18A", "app_debug"})
public final class VideoPostManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.dragonsuite.database.AppDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.dragonsuite.database.dao.VideoPostDao videoPostDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.dragonsuite.service.PlatformConnectionTester platformTester = null;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    
    public VideoPostManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.dragonsuite.model.VideoPost>> getQueuedPosts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.dragonsuite.model.VideoPost>> getScheduledPosts() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object queueVideo(@org.jetbrains.annotations.NotNull()
    android.content.ContentResolver contentResolver, @org.jetbrains.annotations.NotNull()
    android.net.Uri videoUri, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> hashtags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object postVideo(@org.jetbrains.annotations.NotNull()
    android.net.Uri videoUri, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> hashtags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.dragonsuite.service.UploadResult>> $completion) {
        return null;
    }
    
    private final java.util.List<com.example.dragonsuite.service.Account> getAccountsForPlatform(com.example.dragonsuite.service.Platform platform) {
        return null;
    }
    
    private final java.lang.Object uploadToPlatform(com.example.dragonsuite.service.Platform platform, com.example.dragonsuite.service.Account account, android.net.Uri videoUri, java.lang.String title, java.lang.String description, java.util.List<java.lang.String> hashtags, kotlin.coroutines.Continuation<? super com.example.dragonsuite.service.UploadResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearQueue(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearScheduled(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object testVideoUpload(android.content.ContentResolver contentResolver, android.net.Uri videoUri, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object uploadToPlatform(com.example.dragonsuite.service.Platform platform, com.example.dragonsuite.service.Account account, java.io.File videoFile, java.lang.String title, java.lang.String description, java.util.List<java.lang.String> hashtags, kotlin.coroutines.Continuation<? super com.example.dragonsuite.service.UploadResult> $completion) {
        return null;
    }
    
    private final java.io.File createTempFile(android.content.ContentResolver contentResolver, android.net.Uri videoUri) {
        return null;
    }
}