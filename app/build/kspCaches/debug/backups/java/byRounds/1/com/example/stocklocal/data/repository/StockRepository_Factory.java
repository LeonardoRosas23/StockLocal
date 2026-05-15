package com.example.stocklocal.data.repository;

import com.example.stocklocal.data.local.dao.MovementDao;
import com.example.stocklocal.data.local.dao.ProductDao;
import com.example.stocklocal.data.remote.api.OpenFoodApiService;
import com.example.stocklocal.di.TokenProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class StockRepository_Factory implements Factory<StockRepository> {
  private final Provider<ProductDao> productDaoProvider;

  private final Provider<MovementDao> movementDaoProvider;

  private final Provider<OpenFoodApiService> apiServiceProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  public StockRepository_Factory(Provider<ProductDao> productDaoProvider,
      Provider<MovementDao> movementDaoProvider, Provider<OpenFoodApiService> apiServiceProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    this.productDaoProvider = productDaoProvider;
    this.movementDaoProvider = movementDaoProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public StockRepository get() {
    return newInstance(productDaoProvider.get(), movementDaoProvider.get(), apiServiceProvider.get(), preferencesRepositoryProvider.get(), tokenProvider.get());
  }

  public static StockRepository_Factory create(Provider<ProductDao> productDaoProvider,
      Provider<MovementDao> movementDaoProvider, Provider<OpenFoodApiService> apiServiceProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    return new StockRepository_Factory(productDaoProvider, movementDaoProvider, apiServiceProvider, preferencesRepositoryProvider, tokenProvider);
  }

  public static StockRepository newInstance(ProductDao productDao, MovementDao movementDao,
      OpenFoodApiService apiService, PreferencesRepository preferencesRepository,
      TokenProvider tokenProvider) {
    return new StockRepository(productDao, movementDao, apiService, preferencesRepository, tokenProvider);
  }
}
