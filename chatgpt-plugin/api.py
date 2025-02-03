from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import httpx
from urllib.parse import quote

app = FastAPI()

class UploadRequest(BaseModel):
    videoUrl: str
    platforms: List[str]
    isCharity: bool = False
    context: Optional[str] = None

class UploadResponse(BaseModel):
    status: str
    uploadId: str
    deepLink: str

@app.post("/upload")
async def upload_video(request: UploadRequest) -> UploadResponse:
    try:
        # Create the deep link with all parameters
        platforms_json = str(request.platforms).replace("'", '"')
        deep_link = (
            f"chatgptlauncher://example.com/upload"
            f"?video={quote(request.videoUrl)}"
            f"&platforms={quote(platforms_json)}"
            f"&isCharity={str(request.isCharity).lower()}"
        )
        
        if request.context:
            deep_link += f"&context={quote(request.context)}"
        
        return UploadResponse(
            status="initiated",
            uploadId="upload_" + str(hash(request.videoUrl))[-8:],
            deepLink=deep_link
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/status/{upload_id}")
async def get_status(upload_id: str):
    # In a real implementation, this would check the actual upload status
    # For now, return a mock status
    return {
        "status": "UPLOADING",
        "progress": {
            "YOUTUBE_SHORTS": {
                "platform": "YOUTUBE_SHORTS",
                "stage": "UPLOADING",
                "progress": 0.5,
                "message": "Uploading to YouTube..."
            }
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
