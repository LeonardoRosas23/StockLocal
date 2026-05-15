package com.example.stocklocal.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<TokenProvider> tokenProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<TokenProvider> tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(tokenProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<TokenProvider> tokenProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(tokenProvider);
  }

  public static OkHttpClient provideOkHttpClient(TokenProvider tokenProvider) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(tokenProvider));
  }
}
