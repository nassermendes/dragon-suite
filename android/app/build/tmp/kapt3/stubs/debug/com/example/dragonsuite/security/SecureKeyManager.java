package com.example.dragonsuite.security;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\n\u001a\u00020\u000bH\u0002J\b\u0010\f\u001a\u0004\u0018\u00010\rJ\u000e\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0007*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/dragonsuite/security/SecureKeyManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "keyStore", "Ljava/security/KeyStore;", "kotlin.jvm.PlatformType", "sharedPreferences", "Landroid/content/SharedPreferences;", "createMainKey", "", "getOpenAIKey", "", "storeOpenAIKey", "apiKey", "Companion", "app_debug"})
public final class SecureKeyManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEYSTORE_PROVIDER = "AndroidKeyStore";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MAIN_KEY_ALIAS = "DragonSuiteMainKey";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SHARED_PREFS_NAME = "DragonSuiteSecurePrefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String OPENAI_KEY_PREF = "encrypted_openai_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String IV_PREF = "openai_key_iv";
    private final java.security.KeyStore keyStore = null;
    private final android.content.SharedPreferences sharedPreferences = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.dragonsuite.security.SecureKeyManager.Companion Companion = null;
    
    public SecureKeyManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final void createMainKey() {
    }
    
    public final void storeOpenAIKey(@org.jetbrains.annotations.NotNull()
    java.lang.String apiKey) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getOpenAIKey() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/dragonsuite/security/SecureKeyManager$Companion;", "", "()V", "IV_PREF", "", "KEYSTORE_PROVIDER", "MAIN_KEY_ALIAS", "OPENAI_KEY_PREF", "SHARED_PREFS_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}