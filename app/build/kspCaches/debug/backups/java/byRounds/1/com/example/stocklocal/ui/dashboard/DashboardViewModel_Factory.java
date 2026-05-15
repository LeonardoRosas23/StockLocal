package com.example.stocklocal.ui.dashboard;

import com.example.stocklocal.data.repository.PreferencesRepository;
import com.example.stocklocal.data.repository.StockRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<StockRepository> stockRepositoryProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public DashboardViewModel_Factory(Provider<StockRepository> stockRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.stockRepositoryProvider = stockRepositoryProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(stockRepositoryProvider.get(), preferencesRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<StockRepository> stockRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new DashboardViewModel_Factory(stockRepositoryProvider, preferencesRepositoryProvider);
  }

  public static DashboardViewModel newInstance(StockRepository stockRepository,
      PreferencesRepository preferencesRepository) {
    return new DashboardViewModel(stockRepository, preferencesRepository);
  }
}
