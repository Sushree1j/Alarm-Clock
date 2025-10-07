package com.example.securealarm.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AlarmDatabase_Impl extends AlarmDatabase {
  private volatile AlarmDao _alarmDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `alarms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `trigger_at` INTEGER NOT NULL, `repeat_pattern` TEXT, `label` TEXT, `sound_uri` TEXT, `auth_method` TEXT NOT NULL, `auth_data` TEXT NOT NULL, `is_active` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `snooze_minutes` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '42afb97fe2836e3c1a162a39ce176795')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `alarms`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAlarms = new HashMap<String, TableInfo.Column>(10);
        _columnsAlarms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("trigger_at", new TableInfo.Column("trigger_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("repeat_pattern", new TableInfo.Column("repeat_pattern", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("sound_uri", new TableInfo.Column("sound_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("auth_method", new TableInfo.Column("auth_method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("auth_data", new TableInfo.Column("auth_data", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlarms.put("snooze_minutes", new TableInfo.Column("snooze_minutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlarms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlarms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlarms = new TableInfo("alarms", _columnsAlarms, _foreignKeysAlarms, _indicesAlarms);
        final TableInfo _existingAlarms = TableInfo.read(db, "alarms");
        if (!_infoAlarms.equals(_existingAlarms)) {
          return new RoomOpenHelper.ValidationResult(false, "alarms(com.example.securealarm.data.local.AlarmEntity).\n"
                  + " Expected:\n" + _infoAlarms + "\n"
                  + " Found:\n" + _existingAlarms);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "42afb97fe2836e3c1a162a39ce176795", "81fc45267aa199c8000f310994c3e042");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "alarms");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `alarms`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AlarmDao.class, AlarmDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AlarmDao alarmDao() {
    if (_alarmDao != null) {
      return _alarmDao;
    } else {
      synchronized(this) {
        if(_alarmDao == null) {
          _alarmDao = new AlarmDao_Impl(this);
        }
        return _alarmDao;
      }
    }
  }
}
