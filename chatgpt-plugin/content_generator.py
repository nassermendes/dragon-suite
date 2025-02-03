import openai
from typing import Dict, Optional
from enum import Enum

class Platform(str, Enum):
    YOUTUBE_SHORTS = "YOUTUBE_SHORTS"
    INSTAGRAM_REELS = "INSTAGRAM_REELS"
    TIKTOK = "TIKTOK"

class ContentGenerator:
    def __init__(self, api_key: str):
        openai.api_key = api_key

    async def generate_content(self, context: str, platform: Platform, is_charity: bool = False) -> Dict[str, str]:
        """Generate platform-optimized content using GPT-4."""
        
        # Platform-specific instructions
        platform_prompts = {
            Platform.YOUTUBE_SHORTS: {
                "system": """You are a YouTube Shorts content expert. Create engaging, platform-optimized titles and descriptions.
                Follow these rules:
                1. Titles should be attention-grabbing but not clickbait
                2. Keep titles under 60 characters
                3. Include relevant hashtags in description
                4. Optimize for YouTube search and discovery""",
                "format": "Title: [Your catchy title]\nDescription: [Your engaging description with hashtags]"
            },
            Platform.INSTAGRAM_REELS: {
                "system": """You are an Instagram Reels content expert. Create engaging, platform-optimized captions.
                Follow these rules:
                1. Start with an engaging hook
                2. Use line breaks for readability
                3. Include 5-10 relevant hashtags
                4. Keep the tone conversational and authentic""",
                "format": "Caption: [Your engaging caption with hashtags]"
            },
            Platform.TIKTOK: {
                "system": """You are a TikTok content expert. Create viral-worthy captions that drive engagement.
                Follow these rules:
                1. Keep it short and punchy
                2. Use trending hashtags
                3. Include a call-to-action
                4. Make it conversational and authentic""",
                "format": "Caption: [Your viral-worthy caption with trending hashtags]"
            }
        }

        # Add charity context if applicable
        if is_charity:
            charity_context = "This content is for a charity account. Include appropriate messaging about social impact and calls for support."
            platform_prompts[platform]["system"] += f"\n\n{charity_context}"

        prompt = platform_prompts[platform]
        
        try:
            response = await openai.ChatCompletion.acreate(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": prompt["system"]},
                    {"role": "user", "content": f"Generate content for this context: {context}\n\nRespond in this format:\n{prompt['format']}"}
                ],
                temperature=0.7,
                max_tokens=150
            )
            
            content = response.choices[0].message.content
            
            # Parse the response based on platform
            if platform == Platform.YOUTUBE_SHORTS:
                title = content.split("Title: ")[1].split("\n")[0]
                description = content.split("Description: ")[1]
                return {"title": title, "description": description}
            else:
                caption = content.split("Caption: ")[1]
                return {"title": "", "description": caption}
                
        except Exception as e:
            # Fallback content in case of API issues
            return {
                "title": f"Check out this amazing {'charity ' if is_charity else ''}video!",
                "description": f"Watch this {'impactful ' if is_charity else ''}video! {context}\n\n#trending #viral"
            }
