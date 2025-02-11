package com.example.dragonsuite.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001a\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00050\tH\u00c6\u0003J\u000b\u0010!\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003J\t\u0010\"\u001a\u00020\rH\u00c6\u0003J\t\u0010#\u001a\u00020\u000bH\u00c6\u0003Ja\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010%\u001a\u00020\r2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020(H\u00d6\u0001J\t\u0010)\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u000e\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0018R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013\u00a8\u0006*"}, d2 = {"Lcom/example/dragonsuite/model/VideoPost;", "", "id", "", "uri", "", "title", "description", "hashtags", "", "scheduledTime", "Ljava/time/LocalDateTime;", "isQueued", "", "createdAt", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/time/LocalDateTime;ZLjava/time/LocalDateTime;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getDescription", "()Ljava/lang/String;", "getHashtags", "()Ljava/util/List;", "getId", "()J", "()Z", "getScheduledTime", "getTitle", "getUri", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "video_posts")
public final class VideoPost {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String uri = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String description = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> hashtags = null;
    @org.jetbrains.annotations.Nullable()
    private final java.time.LocalDateTime scheduledTime = null;
    private final boolean isQueued = false;
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    
    public VideoPost(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String uri, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> hashtags, @org.jetbrains.annotations.Nullable()
    java.time.LocalDateTime scheduledTime, boolean isQueued, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUri() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getHashtags() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.time.LocalDateTime getScheduledTime() {
        return null;
    }
    
    public final boolean isQueued() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.time.LocalDateTime component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDateTime component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.dragonsuite.model.VideoPost copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String uri, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> hashtags, @org.jetbrains.annotations.Nullable()
    java.time.LocalDateTime scheduledTime, boolean isQueued, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}