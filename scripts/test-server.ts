import express, { Router } from 'express';
import cors from 'cors';

const app = express();
const port = 8081;

app.use(cors());
app.use(express.json());

// Mock routers
const instagramRouter = Router();
const youtubeRouter = Router();
const tiktokRouter = Router();

// Mock Instagram endpoints
instagramRouter.get('/settings/:account', (req, res) => {
  res.json({
    accountType: 'BUSINESS',
    mediaTypes: ['REELS', 'VIDEO', 'IMAGE', 'CAROUSEL'],
    permissions: ['instagram_basic', 'instagram_content_publish'],
    uploadEnabled: true,
    maxVideoDurationMinutes: 15,
    maxVideoSizeMb: 650
  });
});

instagramRouter.get('/upload-status/:account', (req, res) => {
  res.json({
    status: 'completed',
    mediaId: 'test_media_id',
    platform: 'instagram',
    account: req.params.account,
    url: 'https://instagram.com/p/test_media_id'
  });
});

// Mock YouTube endpoints
youtubeRouter.get('/settings/:account', (req, res) => {
  res.json({
    uploadStatus: 'ENABLED',
    privacyStatus: 'public',
    madeForKids: false,
    permissions: ['youtube.upload', 'youtube.force-ssl'],
    maxVideoDurationMinutes: 60,
    maxVideoSizeGb: 128,
    allowedFormats: ['mp4', 'mov', 'avi'],
    defaultCategory: 'Entertainment'
  });
});

youtubeRouter.get('/upload-status/:account', (req, res) => {
  res.json({
    status: 'completed',
    mediaId: 'test_video_id',
    platform: 'youtube',
    account: req.params.account,
    url: 'https://youtube.com/watch?v=test_video_id'
  });
});

// Mock TikTok endpoints
tiktokRouter.get('/settings/:account', (req, res) => {
  res.json({
    accountType: 'BUSINESS',
    uploadEnabled: true,
    permissions: ['video.upload', 'video.publish', 'user.info.basic'],
    maxVideoDurationMinutes: 10,
    maxVideoSizeMb: 512,
    allowedFormats: ['mp4', 'mov'],
    defaultPrivacy: 'public',
    allowComments: true,
    allowDuets: true,
    allowStitch: true
  });
});

tiktokRouter.get('/upload-status/:account', (req, res) => {
  res.json({
    status: 'completed',
    mediaId: 'test_video_id',
    platform: 'tiktok',
    account: req.params.account,
    url: 'https://tiktok.com/@user/video/test_video_id'
  });
});

app.use('/api/instagram', instagramRouter);
app.use('/api/youtube', youtubeRouter);
app.use('/api/tiktok', tiktokRouter);

app.listen(port, () => {
  console.log(`Test server running at http://localhost:${port}`);
});
