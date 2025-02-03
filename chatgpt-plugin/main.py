from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel, HttpUrl
from typing import List, Optional, Dict
import httpx
from urllib.parse import quote
import json
import uuid
import asyncio
from enum import Enum

app = FastAPI()

# In-memory storage for upload status (replace with a database in production)
uploads = {}

class Platform(str, Enum):
    YOUTUBE_SHORTS = "YOUTUBE_SHORTS"
    INSTAGRAM_REELS = "INSTAGRAM_REELS"
    TIKTOK = "TIKTOK"

class UploadRequest(BaseModel):
    videoUrl: str
    platforms: List[Platform]
    isCharity: bool = False
    title: Optional[str] = None
    description: Optional[str] = None
    context: Optional[str] = ""

class UploadStatus(BaseModel):
    status: str
    progress: Dict[str, dict]
    message: Optional[str] = None

class UploadResponse(BaseModel):
    uploadId: str
    deepLink: str
    message: str

def generate_content(context: str, platform: Platform) -> dict:
    """Generate platform-specific content using GPT-4."""
    prompts = {
        Platform.YOUTUBE_SHORTS: "Create a catchy title and description for a YouTube Short. Context: ",
        Platform.INSTAGRAM_REELS: "Create an engaging caption with hashtags for an Instagram Reel. Context: ",
        Platform.TIKTOK: "Create a viral-worthy caption with trending hashtags for TikTok. Context: "
    }
    
    # This would be replaced with actual GPT-4 API call
    return {
        "title": f"Generated title for {platform}",
        "description": f"Generated description for {platform} with context: {context}"
    }

@app.post("/upload", response_model=UploadResponse)
async def upload_video(request: UploadRequest, background_tasks: BackgroundTasks):
    upload_id = str(uuid.uuid4())
    
    # Initialize status for each platform
    uploads[upload_id] = {
        "status": "INITIALIZING",
        "progress": {
            platform: {
                "platform": platform,
                "stage": "PENDING",
                "progress": 0,
                "message": "Waiting to start..."
            } for platform in request.platforms
        }
    }

    # Generate deep link with all parameters
    platforms_json = json.dumps([p.value for p in request.platforms])
    deep_link = (
        f"chatgptlauncher://upload"
        f"?video={quote(request.videoUrl)}"
        f"&platforms={quote(platforms_json)}"
        f"&uploadId={upload_id}"
        f"&isCharity={str(request.isCharity).lower()}"
    )

    if request.context:
        deep_link += f"&context={quote(request.context)}"

    # Generate content for each platform
    for platform in request.platforms:
        content = generate_content(request.context or "", platform)
        deep_link += f"&{platform.value.lower()}_title={quote(content['title'])}"
        deep_link += f"&{platform.value.lower()}_description={quote(content['description'])}"

    # Create response message based on platforms
    platform_names = [p.value.replace("_", " ").title() for p in request.platforms]
    platforms_text = ", ".join(platform_names[:-1]) + (" and " if len(platform_names) > 1 else "") + platform_names[-1]
    
    message = (
        f"I'll help you upload your video to {platforms_text}. "
        f"I've generated optimized content for each platform. "
        "The Android app will open to handle the upload process."
    )

    return UploadResponse(
        uploadId=upload_id,
        deepLink=deep_link,
        message=message
    )

@app.get("/status/{upload_id}", response_model=UploadStatus)
async def get_upload_status(upload_id: str):
    if upload_id not in uploads:
        raise HTTPException(status_code=404, detail="Upload not found")
    
    return UploadStatus(
        status=uploads[upload_id]["status"],
        progress=uploads[upload_id]["progress"]
    )

@app.post("/status/update/{upload_id}")
async def update_status(
    upload_id: str,
    platform: Platform,
    stage: str,
    progress: float,
    message: str
):
    if upload_id not in uploads:
        raise HTTPException(status_code=404, detail="Upload not found")
    
    uploads[upload_id]["progress"][platform] = {
        "platform": platform,
        "stage": stage,
        "progress": progress,
        "message": message
    }
    
    # Update overall status
    all_complete = all(
        p["progress"] == 1.0 
        for p in uploads[upload_id]["progress"].values()
    )
    
    if all_complete:
        uploads[upload_id]["status"] = "COMPLETED"
    
    return {"status": "updated"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
