package com.example.securealarm.data.local;

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
import com.example.securealarm.security.AuthMethod;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
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
public final class AlarmDao_Impl implements AlarmDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AlarmEntity> __insertionAdapterOfAlarmEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<AlarmEntity> __deletionAdapterOfAlarmEntity;

  private final EntityDeletionOrUpdateAdapter<AlarmEntity> __updateAdapterOfAlarmEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetActive;

  public AlarmDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlarmEntity = new EntityInsertionAdapter<AlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `alarms` (`id`,`trigger_at`,`repeat_pattern`,`label`,`sound_uri`,`auth_method`,`auth_data`,`is_active`,`created_at`,`snooze_minutes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTriggerAtMillis());
        if (entity.getRepeatPattern() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getRepeatPattern());
        }
        if (entity.getLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLabel());
        }
        if (entity.getSoundUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSoundUri());
        }
        final String _tmp = __converters.fromAuthMethod(entity.getAuthMethod());
        statement.bindString(6, _tmp);
        statement.bindString(7, entity.getAuthData());
        final int _tmp_1 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getSnoozeMinutes());
      }
    };
    this.__deletionAdapterOfAlarmEntity = new EntityDeletionOrUpdateAdapter<AlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `alarms` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlarmEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAlarmEntity = new EntityDeletionOrUpdateAdapter<AlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `alarms` SET `id` = ?,`trigger_at` = ?,`repeat_pattern` = ?,`label` = ?,`sound_uri` = ?,`auth_method` = ?,`auth_data` = ?,`is_active` = ?,`created_at` = ?,`snooze_minutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTriggerAtMillis());
        if (entity.getRepeatPattern() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getRepeatPattern());
        }
        if (entity.getLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLabel());
        }
        if (entity.getSoundUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSoundUri());
        }
        final String _tmp = __converters.fromAuthMethod(entity.getAuthMethod());
        statement.bindString(6, _tmp);
        statement.bindString(7, entity.getAuthData());
        final int _tmp_1 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getSnoozeMinutes());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfSetActive = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alarms SET is_active = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AlarmEntity alarm, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlarmEntity.insertAndReturnId(alarm);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AlarmEntity alarm, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AlarmEntity alarm, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setActive(final long id, final boolean active,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetActive.acquire();
        int _argIndex = 1;
        final int _tmp = active ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfSetActive.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AlarmEntity>> getAlarms() {
    final String _sql = "SELECT * FROM alarms ORDER BY trigger_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alarms"}, new Callable<List<AlarmEntity>>() {
      @Override
      @NonNull
      public List<AlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "trigger_at");
          final int _cursorIndexOfRepeatPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_pattern");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfSoundUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sound_uri");
          final int _cursorIndexOfAuthMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_method");
          final int _cursorIndexOfAuthData = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_data");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSnoozeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "snooze_minutes");
          final List<AlarmEntity> _result = new ArrayList<AlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTriggerAtMillis;
            _tmpTriggerAtMillis = _cursor.getLong(_cursorIndexOfTriggerAtMillis);
            final String _tmpRepeatPattern;
            if (_cursor.isNull(_cursorIndexOfRepeatPattern)) {
              _tmpRepeatPattern = null;
            } else {
              _tmpRepeatPattern = _cursor.getString(_cursorIndexOfRepeatPattern);
            }
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpSoundUri;
            if (_cursor.isNull(_cursorIndexOfSoundUri)) {
              _tmpSoundUri = null;
            } else {
              _tmpSoundUri = _cursor.getString(_cursorIndexOfSoundUri);
            }
            final AuthMethod _tmpAuthMethod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfAuthMethod);
            _tmpAuthMethod = __converters.toAuthMethod(_tmp);
            final String _tmpAuthData;
            _tmpAuthData = _cursor.getString(_cursorIndexOfAuthData);
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpSnoozeMinutes;
            _tmpSnoozeMinutes = _cursor.getInt(_cursorIndexOfSnoozeMinutes);
            _item = new AlarmEntity(_tmpId,_tmpTriggerAtMillis,_tmpRepeatPattern,_tmpLabel,_tmpSoundUri,_tmpAuthMethod,_tmpAuthData,_tmpIsActive,_tmpCreatedAt,_tmpSnoozeMinutes);
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
  public Object getAlarmById(final long id, final Continuation<? super AlarmEntity> $completion) {
    final String _sql = "SELECT * FROM alarms WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AlarmEntity>() {
      @Override
      @Nullable
      public AlarmEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "trigger_at");
          final int _cursorIndexOfRepeatPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_pattern");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfSoundUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sound_uri");
          final int _cursorIndexOfAuthMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_method");
          final int _cursorIndexOfAuthData = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_data");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSnoozeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "snooze_minutes");
          final AlarmEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTriggerAtMillis;
            _tmpTriggerAtMillis = _cursor.getLong(_cursorIndexOfTriggerAtMillis);
            final String _tmpRepeatPattern;
            if (_cursor.isNull(_cursorIndexOfRepeatPattern)) {
              _tmpRepeatPattern = null;
            } else {
              _tmpRepeatPattern = _cursor.getString(_cursorIndexOfRepeatPattern);
            }
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpSoundUri;
            if (_cursor.isNull(_cursorIndexOfSoundUri)) {
              _tmpSoundUri = null;
            } else {
              _tmpSoundUri = _cursor.getString(_cursorIndexOfSoundUri);
            }
            final AuthMethod _tmpAuthMethod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfAuthMethod);
            _tmpAuthMethod = __converters.toAuthMethod(_tmp);
            final String _tmpAuthData;
            _tmpAuthData = _cursor.getString(_cursorIndexOfAuthData);
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpSnoozeMinutes;
            _tmpSnoozeMinutes = _cursor.getInt(_cursorIndexOfSnoozeMinutes);
            _result = new AlarmEntity(_tmpId,_tmpTriggerAtMillis,_tmpRepeatPattern,_tmpLabel,_tmpSoundUri,_tmpAuthMethod,_tmpAuthData,_tmpIsActive,_tmpCreatedAt,_tmpSnoozeMinutes);
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

  @Override
  public Object getActiveAlarms(final Continuation<? super List<AlarmEntity>> $completion) {
    final String _sql = "SELECT * FROM alarms WHERE is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlarmEntity>>() {
      @Override
      @NonNull
      public List<AlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "trigger_at");
          final int _cursorIndexOfRepeatPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_pattern");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfSoundUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sound_uri");
          final int _cursorIndexOfAuthMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_method");
          final int _cursorIndexOfAuthData = CursorUtil.getColumnIndexOrThrow(_cursor, "auth_data");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfSnoozeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "snooze_minutes");
          final List<AlarmEntity> _result = new ArrayList<AlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTriggerAtMillis;
            _tmpTriggerAtMillis = _cursor.getLong(_cursorIndexOfTriggerAtMillis);
            final String _tmpRepeatPattern;
            if (_cursor.isNull(_cursorIndexOfRepeatPattern)) {
              _tmpRepeatPattern = null;
            } else {
              _tmpRepeatPattern = _cursor.getString(_cursorIndexOfRepeatPattern);
            }
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpSoundUri;
            if (_cursor.isNull(_cursorIndexOfSoundUri)) {
              _tmpSoundUri = null;
            } else {
              _tmpSoundUri = _cursor.getString(_cursorIndexOfSoundUri);
            }
            final AuthMethod _tmpAuthMethod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfAuthMethod);
            _tmpAuthMethod = __converters.toAuthMethod(_tmp);
            final String _tmpAuthData;
            _tmpAuthData = _cursor.getString(_cursorIndexOfAuthData);
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpSnoozeMinutes;
            _tmpSnoozeMinutes = _cursor.getInt(_cursorIndexOfSnoozeMinutes);
            _item = new AlarmEntity(_tmpId,_tmpTriggerAtMillis,_tmpRepeatPattern,_tmpLabel,_tmpSoundUri,_tmpAuthMethod,_tmpAuthData,_tmpIsActive,_tmpCreatedAt,_tmpSnoozeMinutes);
            _result.add(_item);
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
