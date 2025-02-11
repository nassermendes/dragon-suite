package com.example.dragonsuite;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0016J\b\u0010\u0012\u001a\u00020\nH\u0016J\"\u0010\u0013\u001a\u00020\u00142\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0016J\u0010\u0010\u0017\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/dragonsuite/DragonService;", "Landroid/app/Service;", "()V", "chatGPTService", "Lcom/example/dragonsuite/service/ChatGPTService;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "videoPostManager", "Lcom/example/dragonsuite/service/VideoPostManager;", "analyzeVideo", "", "videoUri", "Landroid/net/Uri;", "handleIntent", "intent", "Landroid/content/Intent;", "onBind", "Landroid/os/IBinder;", "onCreate", "onStartCommand", "", "flags", "startId", "postVideo", "Companion", "app_debug"})
public final class DragonService extends android.app.Service {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private com.example.dragonsuite.service.ChatGPTService chatGPTService;
    private com.example.dragonsuite.service.VideoPostManager videoPostManager;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_ANALYZE_VIDEO = "com.example.dragonsuite.action.ANALYZE_VIDEO";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_POST_VIDEO = "com.example.dragonsuite.action.POST_VIDEO";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_VIDEO_URI = "com.example.dragonsuite.extra.VIDEO_URI";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.dragonsuite.DragonService.Companion Companion = null;
    
    public DragonService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void handleIntent(android.content.Intent intent) {
    }
    
    private final void analyzeVideo(android.net.Uri videoUri) {
    }
    
    private final void postVideo(android.net.Uri videoUri) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/example/dragonsuite/DragonService$Companion;", "", "()V", "ACTION_ANALYZE_VIDEO", "", "ACTION_POST_VIDEO", "EXTRA_VIDEO_URI", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}