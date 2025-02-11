package com.example.dragonsuite.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/example/dragonsuite/service/ConversationState;", "", "(Ljava/lang/String;I)V", "IDLE", "WAITING_FOR_TITLE", "WAITING_FOR_DESCRIPTION", "WAITING_FOR_HASHTAGS", "WAITING_FOR_SCHEDULE", "WAITING_FOR_CONFIRMATION", "app_debug"})
public enum ConversationState {
    /*public static final*/ IDLE /* = new IDLE() */,
    /*public static final*/ WAITING_FOR_TITLE /* = new WAITING_FOR_TITLE() */,
    /*public static final*/ WAITING_FOR_DESCRIPTION /* = new WAITING_FOR_DESCRIPTION() */,
    /*public static final*/ WAITING_FOR_HASHTAGS /* = new WAITING_FOR_HASHTAGS() */,
    /*public static final*/ WAITING_FOR_SCHEDULE /* = new WAITING_FOR_SCHEDULE() */,
    /*public static final*/ WAITING_FOR_CONFIRMATION /* = new WAITING_FOR_CONFIRMATION() */;
    
    ConversationState() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.example.dragonsuite.service.ConversationState> getEntries() {
        return null;
    }
}