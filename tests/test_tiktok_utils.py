import pytest
from android.app.src.main.java.com.example.dragonsuite.utils.TikTokUtils import TikTokUtils
from unittest.mock import Mock, patch

class TestTikTokUtils:
    @pytest.fixture
    def tiktok_utils(self):
        return TikTokUtils()
    
    @pytest.mark.asyncio
    async def test_authentication(self, tiktok_utils):
        with patch('TikTokUtils.authenticate') as mock_auth:
            mock_auth.return_value = True
            result = await tiktok_utils.authenticate()
            assert result is True
            mock_auth.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_share_content(self, tiktok_utils):
        with patch('TikTokUtils.shareContent') as mock_share:
            mock_share.return_value = {"status": "success"}
            result = await tiktok_utils.shareContent("test content")
            assert result["status"] == "success"
            mock_share.assert_called_once_with("test content")
