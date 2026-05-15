package com.example.stocklocal.di;

import com.example.stocklocal.data.local.StockLocalDatabase;
import com.example.stocklocal.data.local.dao.MovementDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DatabaseModule_ProvideMovementDaoFactory implements Factory<MovementDao> {
  private final Provider<StockLocalDatabase> databaseProvider;

  public DatabaseModule_ProvideMovementDaoFactory(Provider<StockLocalDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MovementDao get() {
    return provideMovementDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMovementDaoFactory create(
      Provider<StockLocalDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMovementDaoFactory(databaseProvider);
  }

  public static MovementDao provideMovementDao(StockLocalDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMovementDao(database));
  }
}
