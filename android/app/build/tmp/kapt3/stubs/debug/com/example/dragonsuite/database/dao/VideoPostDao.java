package com.example.dragonsuite.database.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0018\u0010\n\u001a\u0004\u0018\u00010\b2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00100\u000fH\'J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00100\u000fH\'J\u0016\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\u0014"}, d2 = {"Lcom/example/dragonsuite/database/dao/VideoPostDao;", "", "clearQueue", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearScheduled", "delete", "post", "Lcom/example/dragonsuite/model/VideoPost;", "(Lcom/example/dragonsuite/model/VideoPost;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getQueuedPosts", "Lkotlinx/coroutines/flow/Flow;", "", "getScheduledPosts", "insert", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface VideoPostDao {
    
    @androidx.room.Query(value = "SELECT * FROM video_posts WHERE isQueued = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.dragonsuite.model.VideoPost>> getQueuedPosts();
    
    @androidx.room.Query(value = "SELECT * FROM video_posts WHERE scheduledTime IS NOT NULL")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.dragonsuite.model.VideoPost>> getScheduledPosts();
    
    @androidx.room.Query(value = "SELECT * FROM video_posts WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.dragonsuite.model.VideoPost> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.dragonsuite.model.VideoPost post, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.example.dragonsuite.model.VideoPost post, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.dragonsuite.model.VideoPost post, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM video_posts WHERE isQueued = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearQueue(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM video_posts WHERE scheduledTime IS NOT NULL")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearScheduled(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}