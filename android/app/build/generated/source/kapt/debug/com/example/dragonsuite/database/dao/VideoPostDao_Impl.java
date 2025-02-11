package com.example.dragonsuite.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.dragonsuite.database.Converters;
import com.example.dragonsuite.model.VideoPost;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VideoPostDao_Impl implements VideoPostDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VideoPost> __insertionAdapterOfVideoPost;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<VideoPost> __deletionAdapterOfVideoPost;

  private final EntityDeletionOrUpdateAdapter<VideoPost> __updateAdapterOfVideoPost;

  private final SharedSQLiteStatement __preparedStmtOfClearQueue;

  private final SharedSQLiteStatement __preparedStmtOfClearScheduled;

  public VideoPostDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVideoPost = new EntityInsertionAdapter<VideoPost>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `video_posts` (`id`,`uri`,`title`,`description`,`hashtags`,`scheduledTime`,`isQueued`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VideoPost entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUri() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUri());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        final String _tmp = __converters.toStringList(entity.getHashtags());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final Long _tmp_1 = __converters.dateToTimestamp(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = entity.isQueued() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final Long _tmp_3 = __converters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfVideoPost = new EntityDeletionOrUpdateAdapter<VideoPost>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `video_posts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VideoPost entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVideoPost = new EntityDeletionOrUpdateAdapter<VideoPost>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `video_posts` SET `id` = ?,`uri` = ?,`title` = ?,`description` = ?,`hashtags` = ?,`scheduledTime` = ?,`isQueued` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VideoPost entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUri() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUri());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        final String _tmp = __converters.toStringList(entity.getHashtags());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final Long _tmp_1 = __converters.dateToTimestamp(entity.getScheduledTime());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = entity.isQueued() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final Long _tmp_3 = __converters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfClearQueue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM video_posts WHERE isQueued = 1";
        return _query;
      }
    };
    this.__preparedStmtOfClearScheduled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM video_posts WHERE scheduledTime IS NOT NULL";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VideoPost post, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVideoPost.insertAndReturnId(post);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VideoPost post, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVideoPost.handle(post);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final VideoPost post, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVideoPost.handle(post);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearQueue(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearQueue.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearQueue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearScheduled(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearScheduled.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearScheduled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VideoPost>> getQueuedPosts() {
    final String _sql = "SELECT * FROM video_posts WHERE isQueued = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"video_posts"}, new Callable<List<VideoPost>>() {
      @Override
      @NonNull
      public List<VideoPost> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfHashtags = CursorUtil.getColumnIndexOrThrow(_cursor, "hashtags");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsQueued = CursorUtil.getColumnIndexOrThrow(_cursor, "isQueued");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VideoPost> _result = new ArrayList<VideoPost>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VideoPost _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            if (_cursor.isNull(_cursorIndexOfUri)) {
              _tmpUri = null;
            } else {
              _tmpUri = _cursor.getString(_cursorIndexOfUri);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final List<String> _tmpHashtags;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfHashtags)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfHashtags);
            }
            _tmpHashtags = __converters.fromStringList(_tmp);
            final LocalDateTime _tmpScheduledTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __converters.fromTimestamp(_tmp_1);
            final boolean _tmpIsQueued;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsQueued);
            _tmpIsQueued = _tmp_2 != 0;
            final LocalDateTime _tmpCreatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __converters.fromTimestamp(_tmp_3);
            _item = new VideoPost(_tmpId,_tmpUri,_tmpTitle,_tmpDescription,_tmpHashtags,_tmpScheduledTime,_tmpIsQueued,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VideoPost>> getScheduledPosts() {
    final String _sql = "SELECT * FROM video_posts WHERE scheduledTime IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"video_posts"}, new Callable<List<VideoPost>>() {
      @Override
      @NonNull
      public List<VideoPost> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfHashtags = CursorUtil.getColumnIndexOrThrow(_cursor, "hashtags");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsQueued = CursorUtil.getColumnIndexOrThrow(_cursor, "isQueued");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VideoPost> _result = new ArrayList<VideoPost>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VideoPost _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            if (_cursor.isNull(_cursorIndexOfUri)) {
              _tmpUri = null;
            } else {
              _tmpUri = _cursor.getString(_cursorIndexOfUri);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final List<String> _tmpHashtags;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfHashtags)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfHashtags);
            }
            _tmpHashtags = __converters.fromStringList(_tmp);
            final LocalDateTime _tmpScheduledTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __converters.fromTimestamp(_tmp_1);
            final boolean _tmpIsQueued;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsQueued);
            _tmpIsQueued = _tmp_2 != 0;
            final LocalDateTime _tmpCreatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __converters.fromTimestamp(_tmp_3);
            _item = new VideoPost(_tmpId,_tmpUri,_tmpTitle,_tmpDescription,_tmpHashtags,_tmpScheduledTime,_tmpIsQueued,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super VideoPost> $completion) {
    final String _sql = "SELECT * FROM video_posts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VideoPost>() {
      @Override
      @Nullable
      public VideoPost call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfHashtags = CursorUtil.getColumnIndexOrThrow(_cursor, "hashtags");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsQueued = CursorUtil.getColumnIndexOrThrow(_cursor, "isQueued");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final VideoPost _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            if (_cursor.isNull(_cursorIndexOfUri)) {
              _tmpUri = null;
            } else {
              _tmpUri = _cursor.getString(_cursorIndexOfUri);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final List<String> _tmpHashtags;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfHashtags)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfHashtags);
            }
            _tmpHashtags = __converters.fromStringList(_tmp);
            final LocalDateTime _tmpScheduledTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfScheduledTime);
            }
            _tmpScheduledTime = __converters.fromTimestamp(_tmp_1);
            final boolean _tmpIsQueued;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsQueued);
            _tmpIsQueued = _tmp_2 != 0;
            final LocalDateTime _tmpCreatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __converters.fromTimestamp(_tmp_3);
            _result = new VideoPost(_tmpId,_tmpUri,_tmpTitle,_tmpDescription,_tmpHashtags,_tmpScheduledTime,_tmpIsQueued,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
