package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u0006H\u0002J\u0010\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0007H\u0016\u00a8\u0006\u000e"}, d2 = {"Lcom/example/dragonsuite/service/DragonFirebaseMessagingService;", "Lcom/google/firebase/messaging/FirebaseMessagingService;", "()V", "handleVideoAnalysis", "", "data", "", "", "onMessageReceived", "message", "Lcom/google/firebase/messaging/RemoteMessage;", "onNewToken", "token", "Companion", "app_debug"})
public final class DragonFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_ANALYZE_VIDEO = "analyze_video";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ACTION = "action";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_VIDEO_URI = "video_uri";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_UPLOAD_URL = "upload_url";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.dragonsuite.service.DragonFirebaseMessagingService.Companion Companion = null;
    
    public DragonFirebaseMessagingService() {
        super();
    }
    
    @java.lang.Override()
    public void onMessageReceived(@org.jetbrains.annotations.NotNull()
    com.google.firebase.messaging.RemoteMessage message) {
    }
    
    @java.lang.Override()
    public void onNewToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    private final void handleVideoAnalysis(java.util.Map<java.lang.String, java.lang.String> data) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/dragonsuite/service/DragonFirebaseMessagingService$Companion;", "", "()V", "ACTION_ANALYZE_VIDEO", "", "KEY_ACTION", "KEY_UPLOAD_URL", "KEY_VIDEO_URI", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}