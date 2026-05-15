package com.example.stocklocal.ui.inventory;

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
public final class InventoryViewModel_Factory implements Factory<InventoryViewModel> {
  private final Provider<StockRepository> repositoryProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public InventoryViewModel_Factory(Provider<StockRepository> repositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public InventoryViewModel get() {
    return newInstance(repositoryProvider.get(), preferencesRepositoryProvider.get());
  }

  public static InventoryViewModel_Factory create(Provider<StockRepository> repositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new InventoryViewModel_Factory(repositoryProvider, preferencesRepositoryProvider);
  }

  public static InventoryViewModel newInstance(StockRepository repository,
      PreferencesRepository preferencesRepository) {
    return new InventoryViewModel(repository, preferencesRepository);
  }
}
