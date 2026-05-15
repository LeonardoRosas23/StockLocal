package com.example.stocklocal.ui.register;

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
public final class RegisterViewModel_Factory implements Factory<RegisterViewModel> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<StockRepository> stockRepositoryProvider;

  public RegisterViewModel_Factory(Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<StockRepository> stockRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.stockRepositoryProvider = stockRepositoryProvider;
  }

  @Override
  public RegisterViewModel get() {
    return newInstance(preferencesRepositoryProvider.get(), stockRepositoryProvider.get());
  }

  public static RegisterViewModel_Factory create(
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<StockRepository> stockRepositoryProvider) {
    return new RegisterViewModel_Factory(preferencesRepositoryProvider, stockRepositoryProvider);
  }

  public static RegisterViewModel newInstance(PreferencesRepository preferencesRepository,
      StockRepository stockRepository) {
    return new RegisterViewModel(preferencesRepository, stockRepository);
  }
}
