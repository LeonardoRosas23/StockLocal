package com.example.stocklocal.ui.movements;

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
public final class MovementViewModel_Factory implements Factory<MovementViewModel> {
  private final Provider<StockRepository> repositoryProvider;

  public MovementViewModel_Factory(Provider<StockRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MovementViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static MovementViewModel_Factory create(Provider<StockRepository> repositoryProvider) {
    return new MovementViewModel_Factory(repositoryProvider);
  }

  public static MovementViewModel newInstance(StockRepository repository) {
    return new MovementViewModel(repository);
  }
}
