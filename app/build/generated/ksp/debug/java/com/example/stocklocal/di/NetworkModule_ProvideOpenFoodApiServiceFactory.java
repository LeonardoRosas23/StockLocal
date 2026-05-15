package com.example.stocklocal.di;

import com.example.stocklocal.data.remote.api.OpenFoodApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideOpenFoodApiServiceFactory implements Factory<OpenFoodApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideOpenFoodApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public OpenFoodApiService get() {
    return provideOpenFoodApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideOpenFoodApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideOpenFoodApiServiceFactory(retrofitProvider);
  }

  public static OpenFoodApiService provideOpenFoodApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOpenFoodApiService(retrofit));
  }
}
