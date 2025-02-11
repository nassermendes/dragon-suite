package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\r\u001a\u00020\t2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0018\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u001a\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0014\u0010\u001b\u001a\u0004\u0018\u00010\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\tH\u0002J\u0014\u0010\u001d\u001a\u0004\u0018\u00010\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\tH\u0002J\u001c\u0010\u001e\u001a\u0004\u0018\u00010\t2\u0006\u0010\u001f\u001a\u00020\u00072\b\u0010\u001c\u001a\u0004\u0018\u00010\tH\u0002J\u0014\u0010 \u001a\u0004\u0018\u00010\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\tH\u0002J0\u0010!\u001a\b\u0012\u0004\u0012\u00020#0\"2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0086@\u00a2\u0006\u0002\u0010$J0\u0010%\u001a\u00020#2\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\b2\u0006\u0010\'\u001a\u00020\u00132\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0082@\u00a2\u0006\u0002\u0010(R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\u0005\u001a\u001a\u0012\u0004\u0012\u00020\u0007\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\f\u00a8\u0006)"}, d2 = {"Lcom/example/dragonsuite/service/SocialMediaUploadService;", "", "()V", "client", "Lokhttp3/OkHttpClient;", "platformEndpoints", "", "Lcom/example/dragonsuite/service/Platform;", "Lcom/example/dragonsuite/service/Account;", "", "platformTokens", "error/NonExistentClass", "Lerror/NonExistentClass;", "buildSocialMediaCaption", "videoAnalysis", "Lcom/example/dragonsuite/service/VideoAnalysis;", "createInstagramRequest", "Lokhttp3/MultipartBody;", "file", "Ljava/io/File;", "createTempFile", "contentResolver", "Landroid/content/ContentResolver;", "videoUri", "Landroid/net/Uri;", "createTikTokRequest", "createYouTubeRequest", "extractInstagramUrl", "response", "extractTikTokUrl", "extractUrlFromResponse", "platform", "extractYouTubeUrl", "uploadToAllPlatforms", "", "Lcom/example/dragonsuite/service/UploadResult;", "(Landroid/content/ContentResolver;Landroid/net/Uri;Lcom/example/dragonsuite/service/VideoAnalysis;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadToPlatform", "account", "videoFile", "(Lcom/example/dragonsuite/service/Platform;Lcom/example/dragonsuite/service/Account;Ljava/io/File;Lcom/example/dragonsuite/service/VideoAnalysis;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SocialMediaUploadService {
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<com.example.dragonsuite.service.Platform, java.util.Map<com.example.dragonsuite.service.Account, java.lang.String>> platformEndpoints = null;
    @org.jetbrains.annotations.NotNull()
    private final error.NonExistentClass platformTokens = null;
    
    public SocialMediaUploadService() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadToAllPlatforms(@org.jetbrains.annotations.NotNull()
    android.content.ContentResolver contentResolver, @org.jetbrains.annotations.NotNull()
    android.net.Uri videoUri, @org.jetbrains.annotations.Nullable()
    com.example.dragonsuite.service.VideoAnalysis videoAnalysis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.dragonsuite.service.UploadResult>> $completion) {
        return null;
    }
    
    private final java.lang.Object uploadToPlatform(com.example.dragonsuite.service.Platform platform, com.example.dragonsuite.service.Account account, java.io.File videoFile, com.example.dragonsuite.service.VideoAnalysis videoAnalysis, kotlin.coroutines.Continuation<? super com.example.dragonsuite.service.UploadResult> $completion) {
        return null;
    }
    
    private final java.io.File createTempFile(android.content.ContentResolver contentResolver, android.net.Uri videoUri) {
        return null;
    }
    
    private final okhttp3.MultipartBody createInstagramRequest(java.io.File file, com.example.dragonsuite.service.VideoAnalysis videoAnalysis) {
        return null;
    }
    
    private final okhttp3.MultipartBody createYouTubeRequest(java.io.File file, com.example.dragonsuite.service.VideoAnalysis videoAnalysis) {
        return null;
    }
    
    private final okhttp3.MultipartBody createTikTokRequest(java.io.File file, com.example.dragonsuite.service.VideoAnalysis videoAnalysis) {
        return null;
    }
    
    private final java.lang.String buildSocialMediaCaption(com.example.dragonsuite.service.VideoAnalysis videoAnalysis) {
        return null;
    }
    
    private final java.lang.String extractUrlFromResponse(com.example.dragonsuite.service.Platform platform, java.lang.String response) {
        return null;
    }
    
    private final java.lang.String extractInstagramUrl(java.lang.String response) {
        return null;
    }
    
    private final java.lang.String extractYouTubeUrl(java.lang.String response) {
        return null;
    }
    
    private final java.lang.String extractTikTokUrl(java.lang.String response) {
        return null;
    }
}