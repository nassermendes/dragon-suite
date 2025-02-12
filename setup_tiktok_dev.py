import os
import subprocess
import json
from pathlib import Path

def run_command(command, cwd=None):
    process = subprocess.run(command, shell=True, cwd=cwd, capture_output=True, text=True)
    if process.returncode != 0:
        print(f"Error executing command: {command}")
        print(f"Error: {process.stderr}")
        return None
    return process.stdout.strip()

def add_tiktok_dependencies():
    build_gradle = "android/app/build.gradle"
    
    with open(build_gradle, 'r') as f:
        content = f.read()
    
    # Add TikTok SDK dependencies if not already present
    dependencies_to_add = """
    // TikTok SDK dependencies
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-sdk:2.3.2'
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-auth:2.3.2'
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-share:2.3.2'
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-core:2.3.2'
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-common:2.3.2'
"""
    
    if 'com.tiktok.open.sdk' not in content:
        dependency_section = content.find('dependencies {')
        if dependency_section != -1:
            insert_pos = content.find('}', dependency_section)
            new_content = content[:insert_pos] + dependencies_to_add + content[insert_pos:]
            
            with open(build_gradle, 'w') as f:
                f.write(new_content)
            print("Added TikTok SDK dependencies to build.gradle")

def add_tiktok_repository():
    project_build_gradle = "android/build.gradle"
    
    with open(project_build_gradle, 'r') as f:
        content = f.read()
    
    # Add TikTok Maven repository if not already present
    repository_to_add = """
        maven { url "https://artifact.bytedance.com/repository/maven" }
"""
    
    if 'bytedance.com' not in content:
        repository_section = content.find('repositories {')
        if repository_section != -1:
            insert_pos = content.find('}', repository_section)
            new_content = content[:insert_pos] + repository_to_add + content[insert_pos:]
            
            with open(project_build_gradle, 'w') as f:
                f.write(new_content)
            print("Added TikTok Maven repository to project build.gradle")

def create_tiktok_utils():
    utils_dir = "android/app/src/main/java/com/example/dragonsuite/utils"
    os.makedirs(utils_dir, exist_ok=True)
    
    tiktok_utils = os.path.join(utils_dir, "TikTokUtils.kt")
    content = """package com.example.dragonsuite.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.tiktok.open.sdk.auth.AuthApi
import com.tiktok.open.sdk.auth.AuthResponse
import com.tiktok.open.sdk.base.ErrorCode
import com.tiktok.open.sdk.base.TikTokOpenApi
import com.tiktok.open.sdk.base.TikTokOpenConfig
import com.tiktok.open.sdk.share.Share

object TikTokUtils {
    private const val TAG = "TikTokUtils"
    private var isInitialized = false
    
    fun initialize(context: Context, clientKey: String) {
        if (!isInitialized) {
            val config = TikTokOpenConfig(clientKey)
            TikTokOpenApi.init(context, config)
            isInitialized = true
            Log.d(TAG, "TikTok SDK initialized")
        }
    }
    
    fun authorize(context: Context, scope: String, callback: (AuthResponse?) -> Unit) {
        val request = AuthApi.Request()
        request.scope = scope
        AuthApi.auth(context, request) { response ->
            when (response.errorCode) {
                ErrorCode.NO_ERROR -> {
                    Log.d(TAG, "Auth success: ${response.authCode}")
                    callback(response)
                }
                ErrorCode.ERROR_NO_INSTALL -> {
                    Log.e(TAG, "TikTok app not installed")
                    callback(null)
                }
                else -> {
                    Log.e(TAG, "Auth failed: ${response.errorMsg}")
                    callback(null)
                }
            }
        }
    }
    
    fun shareVideo(context: Context, videoPath: String, callback: (Boolean) -> Unit) {
        val request = Share.Request()
        request.mediaPath = videoPath
        request.mediaType = Share.MediaType.VIDEO
        Share.share(context, request) { response ->
            when (response.errorCode) {
                ErrorCode.NO_ERROR -> {
                    Log.d(TAG, "Share success")
                    callback(true)
                }
                else -> {
                    Log.e(TAG, "Share failed: ${response.errorMsg}")
                    callback(false)
                }
            }
        }
    }
    
    fun getAppSignatures(context: Context): Array<String> {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            return Array(packageInfo.signatures.size) { i ->
                packageInfo.signatures[i].toCharsString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app signatures", e)
            return emptyArray()
        }
    }
}"""
    
    with open(tiktok_utils, 'w') as f:
        f.write(content)
    print(f"Created TikTok utilities class at {tiktok_utils}")

def update_manifest():
    manifest_file = "android/app/src/main/AndroidManifest.xml"
    
    with open(manifest_file, 'r') as f:
        content = f.read()
    
    # Add TikTok SDK permissions and activities if not already present
    if 'com.tiktok.open.sdk' not in content:
        insert_pos = content.find('</application>')
        tiktok_config = """
        <!-- TikTok SDK Configuration -->
        <activity
            android:name="com.tiktok.open.sdk.auth.AuthActivity"
            android:launchMode="singleTask"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="${applicationId}"
                    android:scheme="tiktok" />
            </intent-filter>
        </activity>
        """
        new_content = content[:insert_pos] + tiktok_config + content[insert_pos:]
        
        with open(manifest_file, 'w') as f:
            f.write(new_content)
        print("Updated AndroidManifest.xml with TikTok configuration")

def main():
    # Ensure we're in the project root
    project_root = Path(__file__).parent
    os.chdir(project_root)
    
    print("Setting up TikTok development environment...")
    
    # Add TikTok SDK dependencies
    add_tiktok_dependencies()
    
    # Add TikTok Maven repository
    add_tiktok_repository()
    
    # Create TikTok utilities class
    create_tiktok_utils()
    
    # Update Android Manifest
    update_manifest()
    
    print("\nTikTok development environment setup complete!")
    print("\nNext steps:")
    print("1. Register your app on TikTok Developer Portal (https://developers.tiktok.com/)")
    print("2. Get your TikTok App ID and Client Key")
    print("3. Update the TikTok configuration in your app")

if __name__ == "__main__":
    main()
