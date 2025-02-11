package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006H\u0082@\u00a2\u0006\u0002\u0010\u0007J$\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\t2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0002\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0013"}, d2 = {"Lcom/example/dragonsuite/service/VideoAnalysisService;", "", "()V", "analyzeFramesWithGPT", "", "frames", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "analyzeVideo", "Lkotlin/Result;", "videoUri", "Landroid/net/Uri;", "analyzeVideo-gIAlu-s", "(Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "convertBitmapToBase64", "bitmap", "Landroid/graphics/Bitmap;", "extractFrames", "Companion", "app_debug"})
public final class VideoAnalysisService {
    private static final int FRAMES_TO_ANALYZE = 5;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.dragonsuite.service.VideoAnalysisService.Companion Companion = null;
    
    public VideoAnalysisService() {
        super();
    }
    
    private final java.util.List<java.lang.String> extractFrames(android.net.Uri videoUri) {
        return null;
    }
    
    private final java.lang.String convertBitmapToBase64(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    private final java.lang.Object analyzeFramesWithGPT(java.util.List<java.lang.String> frames, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/example/dragonsuite/service/VideoAnalysisService$Companion;", "", "()V", "FRAMES_TO_ANALYZE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}